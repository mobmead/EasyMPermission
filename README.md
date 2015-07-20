# EasyMPermission
Code generator for new Android app permission model

[ ![Download](https://api.bintray.com/packages/mobmead/EasyMPermission/EasyMPermission/images/download.svg) ](https://bintray.com/mobmead/EasyMPermission/EasyMPermission/_latestVersion)

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-EasyMPermission-green.svg?style=flat)](https://android-arsenal.com/details/1/2169)

### What is EasyMPermission

EasyMPermission is based on the great [project lombok]. It creates the boilerplate code to

- check required permission at run-time,
- generate permission request code,
- generate callback function onRequestPermissionsResult.

With EasyMPermission app developers don't need to change current app flow. By adding annotations to existing classes and methods EasyMPermission will generate all boilerplate code for you.

For detailed infromation see this [web site] [1]

### How to use EasyMPermission

- Add EasyMPermission repo in gradle file
```groovy
maven {
    url  "http://dl.bintray.com/mobmead/EasyMPermission" 
}
```
- Add below lines into your gradle file's dependencies section
```groovy
provided 'com.mobmead:easympermission:1.0.0'
provided 'org.glassfish:javax.annotation:10.0-b28'
```

### License
Copyright (C) 2015 Jian Chen <jian@mobmead.com>.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

[1]:http://mobmead.github.io/EasyMPermission/
[project lombok]:http://projectlombok.org/