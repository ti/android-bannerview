package com.leenanxi.open;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.leenanxi.open.wdget.BannerView;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private BannerView mBannerView;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

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
                        .placeholder(R.drawable.photo_holder_72dp)
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


}
