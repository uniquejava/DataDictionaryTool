MySQL/Oracle数据字典生成工具
==================
DataDictionaryTool 

基于反编译的代码重写， 升级到jacob1.17以及SWT(Eclipse3.7)

jacob1.18是在JDK1.7下，swt4.7是在JDK1.8下编译的, 为支持JDK1.6，选择了非最新版本.

## 2017年新版使用说明 （支持32/64位）

1. 务必保证机器上安装了JRE1.6及以上版本, 可以到[Oracle官网](http://www.oracle.com/technetwork/java/javase/downloads)去下载, 比如`jre-8u144-windows-x64.exe`，大约60M左右.
2. 务必保证机器上安装了Office Word(2003, 2007, 2013)
3. 点这里下载32位安装文件（适用于windows xp, window7 32位）
4. 点这里下载64位安装文件 （适用于windows7 64位, windows10)
5. 有任何问题请直接在github上提issue或者发邮件至iamcyper@qq.com. 

### 操作说明

	对于MySQL, 总共三步操作：
	一、双击DataDictionaryTool版本号.jar打开图形界面。
	二、MySQL->载入sql脚本文件
	三、MySQL->生成数据字典
	
	对于Oracle,也是三步操作
	一、双击DataDictionaryTool版本号.jar打开图形界面。
	二、Oracle->载入sql脚本文件
	三、Oracle->生成数据字典


**问：什么时候使用A1,A2?**

如果一张表对应一个文件，请使用MySQL->A1, MySQL->A2(支持同时处理多个文件)

**问：什么时候使用B1，B2？**

如果数据库的所有表结构都在一个文件中，请使用MySQL->B1,MySQL->B2

**问：为什么汉字乱码了？**

那是因为打开SQL脚本文件时使用的编码方式不对。默认是使用的系统编码(一般为GBK) 

可以在打开文件前指定一下编码方式，方法是从Load后面的下拉框中换一种编码试试(一般选择留空或UTF-8);

## Developer's Guide
开发环境： Windows7, Eclipse4.7, JDK1.8

1. 为支持JDK1.6请在Eclipse中设置Java Compiler为JDK1.6, 
2. 导出单个大ＪＡＲ文件的方法，以前有个fatjar插件，这个功能已经集成到Eclipse, 直接File > Export Runnable JAR file即可.
3. 如何导出32位版本？将lib/x86_64排除到classpath之外，然后将lib/x86下的jar添加的classpath即可.
4. 如何导出６４位版本？反过来。
5. DLL文件跑哪里去了？代码会动态加载dll，不必再指定java.library.path.

## TODO
1. 各版Office及Windows测试。

## 已经测试过的
1. Windows7 x64 + Office2013
2. Windows XP + Office2007

## 2007旧版使用说明 （只支持32位操作系统）
areha001反编译了我的代码， 因１０年前我的移动硬盘被盗我自己也没有源代码：（

旧版使用帮助：
https://github.com/areha001/DataDictionaryTool/blob/master/src/help.txt

原作者证明， 这一行代码最后有我的ID: uniquejava
https://github.com/areha001/DataDictionaryTool/blob/master/src/ysb/swt/dialog/LRCompositeWithMenu.java#L17

最后FUCK百度， 百度空间中的所有技术博客(2006-2010)全部丢失。
