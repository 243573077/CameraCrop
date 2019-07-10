# CameraCrop
![](https://img.shields.io/badge/%E6%94%AF%E6%8C%81-%E6%A8%AA%E5%B1%8F%2F%E7%AB%96%E5%B1%8F%2F%E4%BB%BB%E6%84%8F%E5%B1%8F%E5%B9%95%E5%B0%BA%E5%AF%B8%2F%E4%BB%BB%E6%84%8F%E7%9B%B8%E6%9C%BA%E9%A2%84%E8%A7%88%E5%B0%BA%E5%AF%B8-green.svg) ![](https://img.shields.io/badge/%E9%80%82%E5%90%88-%E8%BA%AB%E4%BB%BD%E8%AF%81%2FOCR%2F%E8%87%AA%E5%AE%9A%E4%B9%89%E7%9B%B8%E6%9C%BA%E8%A3%81%E5%89%AA-green.svg)
## 自定义相机裁剪指定区域 身份证识别 OCR<br>
可用于身份证，行驶证等证件OCR识别。裁剪指定区域后的bitmap保存为图片，需要自行加载显示。<br>
支持横屏或者竖屏(修改AndroidManifest.xml中Activity的屏幕方向)，不支持页面内横竖屏切换，需要此功能的小伙伴，请自行更改源码实现需求！  
通过搜索发现[IDCardCamera](https://github.com/wildma/IDCardCamera) 是目前网上做得最好的，但该项目不支持竖屏，且裁剪区域有一定偏差。
基于[IDCardCamera](https://github.com/wildma/IDCardCamera) 的预览View及感应器代码，单独实现预览界面指定区域裁剪计算方式(详细内容见代码注释)，支持任意尺寸屏幕，任意相机预览尺寸,任意屏幕方向。  


感谢<br>
[wildma](https://github.com/wildma) 的 [IDCardCamera](https://github.com/wildma/IDCardCamera) 
