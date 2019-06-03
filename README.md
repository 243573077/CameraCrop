# CameraCrop
## 自定义相机裁剪指定区域<br>
可用于身份证，行驶证等证件OCR识别。裁剪指定区域后的bitmap保存为图片，需要自行加载显示。<br>
支持横屏或者竖屏(修改AndroidManifest.xml中Activity的屏幕方向)，不支持页面内横竖屏切换，需要此功能的小伙伴，请自行更改源码实现需求！  
通过搜索发现[IDCardCamera](https://github.com/wildma/IDCardCamera) 是目前网上做得最好的，但该项目不支持竖屏，且裁剪区域有一定偏差。
基于[IDCardCamera](https://github.com/wildma/IDCardCamera) 的预览View及感应器代码，单独实现预览界面指定区域裁剪算法(详细内容见代码注释)，支持任意尺寸屏幕，任意相机预览尺寸,任意屏幕方向。  


感谢<br>
[wildma](https://github.com/wildma) 的 [IDCardCamera](https://github.com/wildma/IDCardCamera) 
