# PopoverView

An Android Library that simulates an popover.

### Gradle

```
compile 'com.github.rtoshiro.popoverview:popoverview:1.0.0' 
```

```
    repositories {
        mavenCentral()
    }
```

Very easy to use.

```
PopoverView popoverView = new PopoverView(this);
popoverView.setContentView(R.layout.popoverview_layout);
```

Then, you can call:

```
popoverView.show(myRefView, PopoverView.PopoverViewPosition.Any);
```

And it will be display automatically near the 'myRefView' view.