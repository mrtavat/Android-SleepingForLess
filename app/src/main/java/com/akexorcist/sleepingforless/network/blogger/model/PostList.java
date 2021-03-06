package com.akexorcist.sleepingforless.network.blogger.model;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by Akexorcist on 3/10/2016 AD.
 */

@Parcel(parcelsIndex = false)
public class PostList {
    String kind;
    String nextPageToken;
    String etag;
    List<Item> items;

    public PostList() {
    }

    public String getKind() {
        return kind;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public String getEtag() {
        return etag;
    }

    public List<Item> getItems() {
        return items;
    }

    @Parcel(parcelsIndex = false)
    public static class Item {
        String kind;
        String id;
        Blog blog;
        String published;
        String updated;
        String etag;
        String url;
        String selfLink;
        String title;
        String content;
        List<Image> images;
        Author author;
        Reply replies;
        List<String> labels;

        public Item() {
        }

        public Item(String title, String id, String url) {
            this.title = title;
            this.id = id;
            this.url = url;
        }

        public String getKind() {
            return kind;
        }

        public String getId() {
            return id;
        }

        public Blog getBlog() {
            return blog;
        }

        public String getPublished() {
            return published;
        }

        public String getUpdated() {
            return updated;
        }

        public String getEtag() {
            return etag;
        }

        public String getUrl() {
            return url;
        }

        public String getSelfLink() {
            return selfLink;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public boolean isContentAvailable() {
            return content != null;
        }

        public List<Image> getImages() {
            return images;
        }

        public boolean isImageAvailable() {
            return images != null && !images.isEmpty() && images.size() > 0;
        }

        public Author getAuthor() {
            return author;
        }

        public Reply getReplies() {
            return replies;
        }

        public List<String> getLabels() {
            return labels;
        }
    }

    @Parcel(parcelsIndex = false)
    public static class Blog {
        String id;

        public Blog() {
        }

        public String getId() {
            return id;
        }
    }

    @Parcel(parcelsIndex = false)
    public static class Author {
        String id;
        String displayName;
        String url;
        Image image;

        public Author() {
        }

        public String getId() {
            return id;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getUrl() {
            return url;
        }

        public Image getImage() {
            return image;
        }
    }

    @Parcel(parcelsIndex = false)
    public static class Image {
        String url;

        public Image() {
        }

        public String getUrl() {
            return url;
        }
    }

    @Parcel(parcelsIndex = false)
    public static class Reply {
        String totalItems;
        String selfLink;

        public Reply() {
        }

        public String getTotalItems() {
            return totalItems;
        }

        public String getSelfLink() {
            return selfLink;
        }
    }
}
