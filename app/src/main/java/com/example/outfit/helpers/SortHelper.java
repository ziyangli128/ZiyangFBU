package com.example.outfit.helpers;

import com.example.outfit.models.Post;

import java.util.List;

public class SortHelper {

    static int partition(List<Post> posts, int begin, int end) {
        int pivot = end;

        int counter = begin;
        for (int i = begin; i < end; i++) {
            if (posts.get(i).getDistanceToCurrent() < posts.get(pivot).getDistanceToCurrent()) {
                Post temp = posts.get(counter);
                posts.set(counter, posts.get(i));
                posts.set(i, temp);
                counter++;
            }
        }
        Post temp = posts.get(pivot);
        posts.set(pivot, posts.get(counter));
        posts.set(counter, temp);

        return counter;
    }

    public static void quickSort(List<Post> posts, int begin, int end) {
        if (end <= begin) return;
        int pivot = partition(posts, begin, end);
        quickSort(posts, begin, pivot-1);
        quickSort(posts, pivot+1, end);
    }
}
