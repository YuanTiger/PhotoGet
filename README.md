## 前言 ##
很多项目中都会有用户修改头像或者类似的功能。

该功能会访问用户的相册、相机来获取图片，然后显示到页面上。

实现该功能还是比较简单的，网上的资料也非常多，简单查阅之后复制粘贴便能实现，但是很多细节其实并不理解。

并且由于Android安全性的提升，包括Android6.0（API 23）的权限系统升级、Android7.0（API 24）的私有文件访问限制，很多地方稍不注意就会发生崩溃。

最近再次用到了这个功能，这次打算用一篇文章来详细记录这个功能点所对应的知识点，并解决掉之前的很多疑问。

## 打开相册 ##
打开手机相册的方式有多种：
第一种：
```
Intent intent = new Intent();
intent.setAction(Intent.ACTION_PICK);
// 设置文件类型
intent.setType("image/*");
activity.startActivityForResult(intent, requestCode);
```
第二种：
```
Intent intent = new Intent();
intent.setAction(Intent.ACTION_GET_CONTENT);
// 设置文件类型
intent.setType("image/*");
activity.startActivityForResult(intent, requestCode);
```
第三种：
```
Intent intent = new Intent();
intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
// 设置文件类型
intent.setType("image/*");
activity.startActivityForResult(intent, requestCode);
```
这几种方式都可以在获取到读取文件权限的前提下，完美实现图片选择。

第三种[ACTION_OPEN_DOCUMENT](https://developer.android.google.cn/reference/android/content/Intent.html#ACTION_OPEN_DOCUMENT)是在Android5.0(API 19)之后新添加的意图，如果使用的话需要进行
```
if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
	//TODO
}
```
我们这里先不介绍[ACTION_OPEN_DOCUMENT](https://developer.android.google.cn/reference/android/content/Intent.html#ACTION_OPEN_DOCUMENT)。

第二种[ACTION_GET_CONTENT](https://developer.android.google.cn/reference/android/content/Intent.html#ACTION_GET_CONTENT)与第一种[ACTION_PICK](https://developer.android.google.cn/reference/android/content/Intent.html#ACTION_PICK)这两个意图类型的作用也非常类似，都是用来获取手机内容，包括联系人、相册等。

通过`intent.setType("image/*")`来指定**MIME Type**，让系统知道要打开的应用。

这里需要注意，必须指定**MIME Type**，否则项目会崩溃：
```
android.content.ActivityNotFoundException: No Activity found to handle Intent { act=android.intent.action.GET_CONTENT }
android.content.ActivityNotFoundException: No Activity found to handle Intent { act=android.intent.action.ACTION_PICK }
```
根据不同的**MIME Type**，可以跳转到不同的应用。

那么这两者有什么区别呢？

[ACTION_GET_CONTENT](https://developer.android.google.cn/reference/android/content/Intent.html#ACTION_GET_CONTENT)与[ACTION_PICK](https://developer.android.google.cn/reference/android/content/Intent.html#ACTION_PICK)的官方解释在这里。

英语比较差，跟着百度翻译看了半天还是不懂。

![](http://7xvzby.com1.z0.glb.clouddn.com/gaoxiao/%E6%83%A8%E4%B8%8D%E5%BF%8D%E7%9D%B9.png)

英语好的同学可自行食用上面的链接，应该不需要翻墙。

两者的区别介绍都写在了[ACTION_GET_CONTENT](https://developer.android.google.cn/reference/android/content/Intent.html#ACTION_GET_CONTENT)，大概是在说：

如果你有一些特定的集合（由URI标识）想让用户选择，使用[ACTION_PICK](https://developer.android.google.cn/reference/android/content/Intent.html#ACTION_PICK)。

如果让用户基于**MIME Type**选择数据，使用[ACTION_GET_CONTENT](https://developer.android.google.cn/reference/android/content/Intent.html#ACTION_GET_CONTENT)。
在平局的情况下，建议使用[ACTION_GET_CONTENT](https://developer.android.google.cn/reference/android/content/Intent.html#ACTION_GET_CONTENT)。

这个还是需要各位看官自己好好理解，我也没能完全了解两者的使用区别。

并且我发现两者返回的Uri格式是不同的：

[关于Android中Uri的介绍，可以参考这篇文章](http://www.jianshu.com/p/5572b42fc63f)。

两种意图分别唤起相册后，选择同一张图片的回调，也就是在onActivityResult中接收：
```
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode != RESULT_OK) {
        return;
    }
    switch (requestCode) {
        case REQUEST_CODE_ALBUM://相册
            Uri dataUri = data.getData();
            Log.i("mengyuanuri","uri:"+dataUri.getScheme()+":"+dataUri.getSchemeSpecificPart());
            break;
    }
}
```

接下来我们来看看两个意图类型下选择同一张照片返回的数据：

[ACTION_GET_CONTENT](https://developer.android.google.cn/reference/android/content/Intent.html#ACTION_GET_CONTENT)：
```
content://com.android.providers.media.documents/document/image:2116
```
[ACTION_PICK](https://developer.android.google.cn/reference/android/content/Intent.html#ACTION_PICK)：
```
content://media/external/images/media/2116
```
没有其他的东西，两者都是返回一个Uri。

为什么不直接返回给我们图片，而是一个Uri呢？

[因为Intent传输有大小的限制。](http://blog.csdn.net/wingichoy/article/details/50679322)
所以我们需要根据Uri来获取到文件的具体路径。

但是我们发现，就算是同一张照片，两种意图下，返回的Uri也是不一致的。

这主要是因为Uri在Android中的类型也分为很多种，比如这两个意图的Uri种类就不一致。

这里就不做赘述了，我们可以通过网上大神封装的解析Uri的方法将它们统一转化成File路径：
```
public static String getPath( final Uri uri) {
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(App.context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(App.context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(App.context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(App.context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
```
调用完成后，会发现不同的Uri对应的是同一个文件路径：
```
/storage/emulated/0/temp/kouliang_avatar.jpg
```
断点跟进该方法，会发现两个Uri走的是不同的if判断。


简单来说，三种方法都可以使用，并且三种方法都是在onActivityResult中返回Uri，而不是图片。

一般情况使用[ACTION_GET_CONTENT](https://developer.android.google.cn/reference/android/content/Intent.html#ACTION_GET_CONTENT)的会多一些。

## 相机 ##
打开相机的方式：
```
//指定相机意图
Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//设置相片保存的地址
intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
activity.startActivityForResult(intent, requestCode);
```
相机图片的获取方式不同于相册，相机图片获取需要先指定图片的保存路径，在拍摄成功后，我们只需直接去指定路径获取即可:
```
switch (requestCode) {
    //相册
    case REQUEST_CODE_ALBUM:
        Uri dataUri = data.getData();
        Log.i("mengyuanuri", "相册uri:" + dataUri.getScheme() + ":" + dataUri.getSchemeSpecificPart());
        break;
    //相机，注意，相机的回调中Intent为空，不要使用
    case REQUEST_CODE_CAMER:
        File bgPath = Constant.bgPath;
        Bitmap bitmap = BitmapFactory.decodeFile(bgPath.getPath());
        iv_bg.setImageBitmap(bitmap);
        break;
}
```
非常简单，在相机回调中去指定路径中读取图片并显示。

但是我们应该可以想到，**有些手机没有相机**，也就是没有[MediaStore.ACTION_IMAGE_CAPTURE](https://developer.android.google.cn/reference/android/provider/MediaStore.html#ACTION_IMAGE_CAPTURE)意图对应的应用。

如果没有对其进行判断就会抛出[ActivityNotFound](https://developer.android.google.cn/reference/android/content/ActivityNotFoundException.html)的异常。

如何解决这个问题：

1. try-catch，简单粗暴；

2. 通过PackageManager去查询[MediaStore.ACTION_IMAGE_CAPTURE](https://developer.android.google.cn/reference/android/provider/MediaStore.html#ACTION_IMAGE_CAPTURE)意图是否存在。

两种做法都很简单，这里展示如何用PackageManager：
```
/**
 * 判断某个意图是否存在
 */
public static boolean isHaveCame(String intentName) {
    PackageManager packageManager = App.context.getPackageManager();
    Intent intent = new Intent(intentName);
    List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
    return list.size() > 0;
}
```
接着我们运行，十分成功。

但是在7.1的虚拟机中，打开相机崩溃了：
```
android.os.FileUriExposedException: file:///storage/emulated/0/photo_bg.jpg exposed beyond app through ClipData.Item.getUri(
    at android.os.StrictMode.onFileUriExposed(StrictMode.java:1799)
    at android.net.Uri.checkFileUriExposed(Uri.java:2346)
    at android.content.ClipData.prepareToLeaveProcess(ClipData.java:845)
    at android.content.Intent.prepareToLeaveProcess(Intent.java:8941)
    at android.content.Intent.prepareToLeaveProcess(Intent.java:8926)
    at android.app.Instrumentation.execStartActivity(Instrumentation.java:1517)
    at android.app.Activity.startActivityForResult(Activity.java:4225)
    at android.support.v4.app.BaseFragmentActivityJB.startActivityForResult(BaseFragmentActivityJB.java:54)
    at android.support.v4.app.FragmentActivity.startActivityForResult(FragmentActivity.java:75)
    at android.app.Activity.startActivityForResult(Activity.java:4183)
    at android.support.v4.app.FragmentActivity.startActivityForResult(FragmentActivity.java:708)
    at com.my.photoget.utils.AppUtils.startCamer(AppUtils.java:37)
```
崩溃的主要原因是因为在7.0(API 24)中对文件读取进行了安全性的提升，[这篇文章详细介绍了解决方案](http://www.jianshu.com/p/3f9e3fc38eae)。

这里提一下，这和当初Android6.0(API 23)权限管理改版一致，如果**build.gradle**中的`targetSdkVersion`<23，则会沿用以前的权限管理机制，无需进行权限管理改版，[权限管理详见这篇小文](http://www.jianshu.com/p/9271efd71450)。

同理，这里如果你的`targetSdkVersion`<24的话，则无需进行上述崩溃的适配。

但是更新一定是往更好的方向去的，还是建议各位看官及时更新，及时适配，保证`targetSdkVersion`为最新SDK。

## 裁剪 ##
裁剪功能是可选功能，如果想要对获取到的图片进行裁剪，我们可以继续使用裁剪Intent来对图片进行裁剪：
```
Intent intent = new Intent("com.android.camera.action.CROP");
//设置要裁剪的图片Uri
intent.setDataAndType(cropBean.dataUri, "image/*");
//配置一系列裁剪参数
intent.putExtra("outputX", cropBean.outputX);
intent.putExtra("outputY", cropBean.outputY);
intent.putExtra("scale", cropBean.scale);
intent.putExtra("aspectX", cropBean.aspectX);
intent.putExtra("aspectY", cropBean.aspectY);
intent.putExtra("outputFormat", cropBean.outputFormat);
intent.putExtra("return-data", cropBean.isReturnData);
intent.putExtra("output", cropBean.saveUri);
//跳转
activity.startActivityForResult(intent, requestCode);
```
[裁剪参数的含义可以参考这篇文章](https://github.com/showdy/Android_Note/blob/master/showdy_note/android/strategy/%E4%BD%BF%E7%94%A8%E7%B3%BB%E7%BB%9F%E7%9B%B8%E5%86%8C%E5%9B%BE%E7%89%87%E6%88%96%E6%8B%8D%E7%85%A7%E5%B9%B6%E8%A3%81%E5%89%AA%E4%B9%8BAndroid_N%E9%80%82%E9%85%8D.md)：

| 附加选项  | 数据类型 | 描述 |
| :------------ |:---------------:| -----:|
| crop     | String | 发送裁剪信号 |
| aspectX     | int       |X方向上的比例   |
|aspectY|int|Y方向上的比例|
|outputX|int|裁剪区的宽|
|outputY|int|裁剪区的高|
|scale|boolean|是否保留比例|
|return-data|boolean|是否将数据保留在Bitmap中返回|
|data|Parcelable|相应的Bitmap数据|
|circleCrop|String|圆形裁剪区域|
|output|URI|将URI指向相应的file://|
|outputFormat|String|图片输出格式|
|noFaceDetection|boolean| 是否取消人脸识别|

每个属性的解释都很清晰，这里我将裁剪参数封装为了一个Bean对象：
```
public class CropBean {

    //要裁剪的图片Uri
    public Uri dataUri;

    //裁剪宽度
    public int outputX;
    //裁剪高度
    public int outputY;

    //X方向上的比例
    public int aspectX;
    //Y方向上的比例
    public int aspectY;

    //是否保留比例
    public boolean scale;

    //是否将数据保存在Bitmap中返回
    public boolean isReturnData;
    //相应的Bitmap数据
    public Parcelable returnData;

    //如果不需要将图片在Bitmap中返回，需要传递保存图片的Uri
    public Uri saveUri;

    //圆形裁剪区域
    public String circleCrop;

    //图片输出格式，默认JPEG
    public String outputFormat = Bitmap.CompressFormat.JPEG.toString();

    //是否取消人脸识别
    public boolean noFaceDetection;

    /**
     * 根据宽高计算裁剪比例
     */
    public void caculateAspect() {

        scale = true;

        if (outputX == outputY) {
            aspectX = 1;
            aspectY = 1;
            return;
        }
        float proportion = (float) outputX / (float) outputY;

        aspectX = (int) (proportion * 100);
        aspectY = 100;
    }
}
```
关于封装对象中`caculateAspect()`方法，因为**aspectX**与**aspectY**是用来设定裁剪框宽高比例的，所以我选择在指定完**outputX**与**outputY**（也就是裁剪图片的宽度和高度）之后，直接根据宽高来计算裁剪框的大小。

`caculateAspect()`中就是具体的计算过程。

还有几个比较重要的参数需要提一下：

- intent.setData(Uri uri)是必须指定的，它代表着要裁剪的图片的Uri。

- **return-data**参数代表是否要返回数据，如果为true，则返回Bitmap对象，如果为false，则会将图片直接保存到另一个参数**output**中。也就是说，当**return-data**为true时，**output**是没有用的，直接在**onActivityResult**中取data当中的Bitmap即可。如果为false，则直接在onActivityResult中去之前指定到**output**中的地址取出图片即可。

- 综上一点，强烈建议设置**return-data**为false并且设置**output**，[因为Intent传输是有大小限制的](http://blog.csdn.net/wingichoy/article/details/50679322)。为防止超出大小的现象发生，通过Uri传输最为安全。

## 总结 ##
到此为止，获取图片显示的功能已经完成了。

[整个项目已经上传至GitHub](https://github.com/z593492734/PhotoGet)，简单总结一下：

1. 通过相册获取图片的方式有很多，但是在onActivityResult中都是以Uri的方式传递的。

2. 裁剪功能不是必要的，如果没有裁剪需求可忽略。强烈建议不要将**return-data**设置为true，可能会超出Intent传输大小限制。

3. 当你的targetSdkVersion>=23时，需要进行[权限管理的升级](http://www.jianshu.com/p/9271efd71450)，当你的targetSdkVersion>=24时，需要进行[FileProvider的适配](http://www.jianshu.com/p/3f9e3fc38eae)。强烈建议进行适配，提升应用的安全性。



## 感谢 ##
[使用系统裁剪](https://github.com/showdy/Android_Note/blob/master/showdy_note/android/strategy/%E4%BD%BF%E7%94%A8%E7%B3%BB%E7%BB%9F%E7%9B%B8%E5%86%8C%E5%9B%BE%E7%89%87%E6%88%96%E6%8B%8D%E7%85%A7%E5%B9%B6%E8%A3%81%E5%89%AA%E4%B9%8BAndroid_N%E9%80%82%E9%85%8D.md)

[Intent传输大小实战](http://blog.csdn.net/wingichoy/article/details/50679322)

[相机7.0图片选择适配](http://www.jianshu.com/p/3f9e3fc38eae)
