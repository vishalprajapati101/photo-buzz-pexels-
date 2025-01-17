package com.imagevault.wallpapersinfinity.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

public class PexelsResponse {
    private List<Photo> photos;

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public static class Photo implements Parcelable { // Implement Parcelable

        private Src src;

        protected Photo(Parcel in) {
            // Read from the parcel
            this.src = in.readParcelable(Src.class.getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            // Write the fields to the parcel
            dest.writeParcelable(src, flags);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Photo> CREATOR = new Creator<Photo>() {
            @Override
            public Photo createFromParcel(Parcel in) {
                return new Photo(in);
            }

            @Override
            public Photo[] newArray(int size) {
                return new Photo[size];
            }
        };

        public Src getSrc() {
            return src;
        }

        public void setSrc(Src src) {
            this.src = src;
        }

        public static class Src implements Parcelable { // Implement Parcelable for Src

            private String original;

            protected Src(Parcel in) {
                original = in.readString();
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(original);
            }

            @Override
            public int describeContents() {
                return 0;
            }

            public static final Creator<Src> CREATOR = new Creator<Src>() {
                @Override
                public Src createFromParcel(Parcel in) {
                    return new Src(in);
                }

                @Override
                public Src[] newArray(int size) {
                    return new Src[size];
                }
            };

            public String getOriginal() {
                return original;
            }

            public void setOriginal(String original) {
                this.original = original;
            }
        }
    }
}
