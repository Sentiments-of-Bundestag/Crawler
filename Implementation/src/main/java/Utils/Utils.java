package Utils;

import model.Sitzung.Rede;
import model.Sitzung.RedeTeil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.zip.DataFormatException;

public class Utils {
    public static Date StringToDate(String s){
        if(s == null || s.isBlank()){
            return null;
        }
        try{
            return new SimpleDateFormat("dd.MM.yyyy").parse(s);
        }
        catch(ParseException e){
            System.out.println(e.toString());
            return null;
        }
    }

    private static int partition(Integer[] arr, int low, int high)
    {
        int pivot = arr[high];
        int i = (low-1); // index of smaller element
        for (int j=low; j<high; j++)
        {
            // If current element is smaller than or
            // equal to pivot
            if (arr[j] <= pivot)
            {
                i++;

                // swap arr[i] and arr[j]
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }

        // swap arr[i+1] and arr[high] (or pivot)
        int temp = arr[i+1];
        arr[i+1] = arr[high];
        arr[high] = temp;

        return i+1;
    }


    /* The main function that implements QuickSort()
    arr[] --> Array to be sorted,
    low --> Starting index,
    high --> Ending index */
    public static Integer[] sort(Integer[] arr, int low, int high)
    {
        if (low < high)
        {
			/* pi is partitioning index, arr[pi] is
			now at right place */
            int pi = partition(arr, low, high);

            // Recursively sort elements before
            // partition and after partition
            sort(arr, low, pi-1);
            sort(arr, pi+1, high);
        }
        return arr;
    }

    public static List<RedeTeil> listSort(List<RedeTeil> list) {
        return listSort(list, 0, list.size() - 1);
    }

    public static List<RedeTeil> listSort(List<RedeTeil> list, int from, int to) {
        if (from < to) {
            int pivot = from;
            int left = from+1;
            int right = to;
            int pivotValue = list.get(pivot).getZeile_nr();
            while (left <= right) {
                // left <= to -> limit protection
                while (left <= to && pivotValue >= list.get(left).getZeile_nr()) {
                    left++;
                }
                // right > from -> limit protection
                while (right > from && pivotValue < list.get(right).getZeile_nr()) {
                    right--;
                }
                if (left < right) {
                    Collections.swap(list, left, right);
                }
            }
            Collections.swap(list, pivot, left - 1);
            listSort(list, from, right - 1); // <-- pivot was wrong!
            listSort(list, right + 1, to);   // <-- pivot was wrong!
        }
        return list;
    }
}
