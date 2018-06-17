# SSM 是Android SpannableString 基础管理工具，你可以使用它快速制作颜色、字体大小、点击事件、前景色等等功能。

<p align="left">
   <a href="https://jitpack.io/#whamu2/SSM">
    <img src="https://jitpack.io/v/whamu2/SSM.svg" alt="Latest Stable Version" />
  </a>
  <a href="https://developer.android.com/about/versions/android-4.4.html">
    <img src="https://img.shields.io/badge/API-19%2B-blue.svg?style=flat-square" alt="Min Sdk Version" />
  </a>
  <a href="https://opensource.org/licenses/MIT">
    <img src="https://img.shields.io/badge/License-MIT-blue.svg?style=flat-square" alt="License" />
  </a>
  <a href="https://github.com/whamu2">
    <img src="https://img.shields.io/badge/Author-whamu2-orange.svg?style=flat-square" alt="Author" />
  </a>
</p>

## Getting started

### Setting up the dependency

Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:

```groovy
allprojects {
	repositories {
		maven { url 'https://jitpack.io' }
	}
}
```

Step 2. Add the dependency

```groovy
dependencies {
	implementation 'com.github.whamu2:SSM:v1.1.0'
}
```

## 使用

```java
import com.github.whamu2.android.ssm.SpannableStringManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv = findViewById(R.id.tv);

        SpannableString keyWordSpan = SpannableStringManager.getKeyWordSpan(
                ContextCompat.getColor(this, R.color.colorAccent),
                "SpannableString工具能满足日常基础开发",
                "SpannableString"
        );

        tv.setText(keyWordSpan);
    }
}
```

