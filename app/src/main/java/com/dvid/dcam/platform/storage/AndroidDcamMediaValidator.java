package com.dvid.dcam.platform.storage;

import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import java.io.File;

/** Uses Android decoders to reject incomplete recovery candidates before publication. */
final class AndroidDcamMediaValidator implements DcamMediaValidator {
    @Override public boolean isPlayable(DcamFileType type, File file) {
        if (!file.isFile() || file.length() <= 0L) return false;
        if (type == DcamFileType.IMAGE) return isDecodableImage(file);
        if (type == DcamFileType.VIDEO || type == DcamFileType.SOS) return isPlayableVideo(file);
        if (type == DcamFileType.AUDIO) return isPlayableAudio(file);
        return false;
    }

    private static boolean isDecodableImage(File file) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        return options.outWidth > 0 && options.outHeight > 0;
    }

    private static boolean isPlayableVideo(File file) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(file.getAbsolutePath());
            String hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            return "yes".equalsIgnoreCase(hasVideo)
                    && duration != null && Long.parseLong(duration) > 0L;
        } catch (RuntimeException invalid) {
            return false;
        } finally {
            try { retriever.release(); } catch (Exception ignored) { }
        }
    }

    private static boolean isPlayableAudio(File file) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(file.getAbsolutePath());
            String hasAudio = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO);
            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            return "yes".equalsIgnoreCase(hasAudio)
                    && duration != null && Long.parseLong(duration) > 0L;
        } catch (RuntimeException invalid) {
            return false;
        } finally {
            try { retriever.release(); } catch (Exception ignored) { }
        }
    }
}
