# Notes | FAQ

**Page ID**: 26411028  
**Version**: 5  
**Type**: page  
**URL**: https://ducviet.atlassian.net/wiki/spaces/DVID/pages/26411028

---


Tiêu chí xác định 1 camera hợp lệ:
1. Validate phần cứng:
Model 1:  
ro.product.model: BodyCamera
ro.product.device: k69v1_64_k419
ro.board.platform: mt6768
Model 2:  
ro.product.model: BWC
ro.product.device: k69v1_64_k419
ro.board.platform: mt6768
2. Tồn tại file /sdcard/DCIM/configs.cson và tồn tại camera id trong file config (account.user_id="……..")
3. Tồn tại thư mục /sdcard/Android/data/com.bodycamera.nettysocket

Camera id sẽ dùng để xác định 1 đối tượng trong logic.
Vd: cùng 1 mã phần cứng, khi set camera ID là 000001, rồi rút ra, thay đổi sang 000002, ứng dụng sẽ xem đây là 2 camera hoàn toàn khác nhau.

--------------

Tài khoản admin mặc định:
....
....

--------------

Tài khoản user được tự động tạo khi đồng bộ:
(username get từ video)
pass: ......

--------------

Tài khoản Loggly: 
Đăng nhập: [https://bdmadvid.loggly.com/](https://bdmadvid.loggly.com/)
email: [hadinhnhan1102@gmail.com](mailto:hadinhnhan1102@gmail.com)
pass: A!94T6b2Cjc*a1Vn#

token để test local: ......
Setup loggly token trên IntelliJ:

--------------

Thư mục data của app:
C:\Users\{username}\AppData\Local\bdma\
(logs & database & temp files (adb/sqlite/…))

--------------

Khi app phát hiện thiết bị mới, sẽ theo dõi kết nối trong một quãng thời gian vài giây, nếu không bị disconnect giữa chừng thì sẽ tiến hành validate thiết bị & bắn event CONNECTED nếu hợp lệ.

Khi device đang ở trạng thái CONNECTED mà app phát hiện phần cứng ngắt kết nối, sẽ chuyển trạng thái thiết bị sang DISCONNECTING và theo dõi trong vài giây (các lệnh Adb sẽ hold), nếu có kết nối lại sẽ chuyển trạng thái về lại CONNECTED và tiếp tục xử lý, nếu hết thời hạn mà vẫn disconnect thì sẽ bắn event DISCONNECTED, huỷ bỏ các lệnh adb đang hold.

(xem deviceTracker.java & adbClient.java)

--------------

Khi code nên import chính xác class cần dùng, không dùng import all (*):

Dễ đọc & review code hơn

import java.util.List;
import java.util.Map;
import java.util.HashMap;
người đọc biết ngay file đang dùng những class gì.

So với:

```
import java.util.*;
```

muốn biết đang dùng gì phải đọc hết code.

Tránh xung đột tên class

Ví dụ:

import java.util.*;
import java.awt.*;
Cả hai package đều có:

```
List
```

Khi đó:

```
List list;
```

sẽ bị ambiguous.

Import cụ thể:

```
import java.util.List;
```

giúp tránh các lỗi kiểu này.

Giảm diff vô nghĩa khi refactor

Ví dụ ban đầu:

```
import java.util.*;
```

Sau này xóa hết code dùng HashMap.

Import vẫn là:

```
import java.util.*;
```

Không ai biết thực tế dependency đã thay đổi.

Ngược lại với explicit import:

import java.util.HashMap;
import java.util.List;
IDE sẽ warning xóa HashMap khi không dùng nữa.

Danh sách import phản ánh đúng dependency thực tế của file.