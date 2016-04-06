# Android Banner Best Practice

# feature

  - custom image loaddder
  - banner title
  - clickable
  - single class


# how to

1. in layout

```xml
 <com.leenanxi.open.wdget.BannerView
            android:id="@+id/banner"
            android:layout_width="match_parent"
            android:layout_height="180dp"/>

```

2. In Java
```java
             private void initViews(View parentView) {
             List<BannerView.BannerItem> bannerItems = new ArrayList<>();
             BannerView.BannerItem item1 = new BannerView.BannerItem("切, 你是否能在天堂看到人来人往","https://img3.doubanio.com/view/photo/photo/public/p2327027746.jpg");
             BannerView.BannerItem item2 = new BannerView.BannerItem("墙头马上的相册-古巴图志.生活", "https://img3.doubanio.com/view/photo/photo/public/p2327027694.jpg");
             bannerItems.add(item1);
             bannerItems.add(item2);
             bannerItems.add(item1);
             bannerItems.add(item2);

             BannerView bannerView = (BannerView) parentView.findViewById(R.id.banner);
             bannerView.setTitleEnabled(true);
             bannerView.setBannerItems(bannerItems);
             bannerView.startLoop();
             bannerView.setOnItemClickListener(new BannerView.OnItemClickListener() {
                 @Override
                 public void onClick(int position) {
                     Toast.makeText(getContext(),"You click " + String.valueOf(position),Toast.LENGTH_SHORT).show();
                 }
             });
             /**
              * Custom Your Image Loader
              */
             bannerView.setImageLoadder(new BannerView.ImageLoader(){
                 @Override
                 public void loadImage(ImageView imageView, String url) {
                     Glide.with(imageView.getContext())
                             .load(url)
                             .placeholder(R.drawable.image_place_holder)
                             .dontAnimate()
                             .dontTransform()
                             .into(imageView);
                 }
             });
         }

         @Override
         public void onDestroyView() {
             mBannerView.stopLoop();
             super.onDestroyView();

         }
```

# screenshots

![screen shots](screenshots/screen.png)







