package com.dvid.dcam.platform.storage;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.core.content.FileProvider;
import com.dvid.dcam.core.logging.application.port.LogSink;
import com.dvid.dcam.feature.media.application.port.MediaOpener;
import java.io.File;

/** Android implementation for opening a sandboxed DCAM media file. */
public final class AndroidMediaOpenerImpl implements MediaOpener {
    private final Context context;
    private final File root;
    private final LogSink log;

    public AndroidMediaOpenerImpl(Context context, DcamStorage storage, LogSink log) {
        this.context = context;
        this.root = storage.rootDirectory();
        this.log = log;
    }

    @Override public boolean open(String relativePath, String mimeType) {
        try {
            File file = resolveInsideRoot(relativePath);
            Uri uri = FileProvider.getUriForFile(
                    context, context.getPackageName() + ".files", file);
            Intent intent = new Intent(Intent.ACTION_VIEW)
                    .setDataAndType(uri, mimeType)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException | IllegalArgumentException | SecurityException error) {
            log.warn("Could not open media " + relativePath, error);
            return false;
        } catch (Exception error) {
            log.error("Could not resolve media " + relativePath, error);
            return false;
        }
    }

    private File resolveInsideRoot(String relativePath) throws Exception {
        File canonicalRoot = root.getCanonicalFile();
        File candidate = new File(canonicalRoot, relativePath).getCanonicalFile();
        String rootPath = canonicalRoot.getPath() + File.separator;
        if (!candidate.getPath().startsWith(rootPath) || !candidate.isFile()) {
            throw new SecurityException("Media path is outside the managed root");
        }
        return candidate;
    }
}
