package br.com.tattobr.android.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class FileSystemUtil {
    public static String[] labels;
    public static String[] paths;
    public static int count = 0;

    private static ArrayList<String> mounts = new ArrayList<String>();

    public static void determineStorageOptions(Context context) {
        readVoldFile(context.getApplicationContext());
        readMounts(context.getApplicationContext());
        testAndCleanList(context.getApplicationContext());
        setProperties(context.getApplicationContext());
    }

    private static void readMounts(Context context) {
        try {
            File mountFile = new File("/proc/mounts");
            if(mountFile.exists()){
                Scanner scanner = new Scanner(mountFile);
                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    if (line.startsWith("/dev/block/vold/")) {
                        String[] lineElements = line.split(" ");
                        String element = lineElements[1];

                        // don't add the default mount path
                        // it's already in the list.
                        if (!mounts.contains(element))
                            mounts.add(element);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String findBiggestPartition() {
        long biggest = 0l;
        String biggestPath = null;
        String[] paths = FileSystemUtil.paths;
        int size = paths != null ? paths.length : 0;
        long current;
        for (int i = 0; i < size; i++) {
            current = FileSystemUtil.getPartitionSize(paths[i]);
            if (current > biggest) {
                biggest = current;
                biggestPath = paths[i];
            }
        }
        if (biggestPath == null || biggestPath.length() == 0) {
            try {
                biggestPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            } catch (Exception e) {

            }
        }
        return biggestPath;
    }

    public static long getFolderSize(File directory) {
        long length = 0;
        if (directory.isDirectory() && directory.exists()) {
            File[] files = directory.listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    length += file.length();
                } else {
                    length += getFolderSize(file);
                }
            }
        }
        return length;
    }

    public static long getPartitionAvailableSize(String path) {
        StatFs stat = new StatFs(path);
        long blockSize = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 ? stat.getBlockSizeLong() : stat.getBlockSize();
        long available = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 ? stat.getAvailableBlocksLong() * blockSize : stat.getAvailableBlocks() * blockSize;
        return available;
    }

    public static long getPartitionSize(String path) {
        StatFs stat = new StatFs(path);
        long blockSize = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 ? stat.getBlockSizeLong() : stat.getBlockSize();
        long partitionSize = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 ? stat.getBlockCountLong() * blockSize : stat.getBlockCount() * blockSize;
        return partitionSize;
    }

    private static void readVoldFile(Context sContext) {
        /*
         * Scan the /system/etc/vold.fstab file and look for lines like this:
		 * dev_mount sdcard /mnt/sdcard 1
		 * /devices/platform/s3c-sdhci.0/mmc_host/mmc0
		 *
		 * When one is found, split it into its elements and then pull out the
		 * path to the that mount point and add it to the arraylist
		 *
		 * some devices are missing the vold file entirely so we add a path here
		 * to make sure the list always includes the path to the first sdcard,
		 * whether real or emulated.
		 */

        mounts.clear();
        mounts.add(Environment.getExternalStorageDirectory().getAbsolutePath());

        try {
            Scanner scanner = new Scanner(new File("/system/etc/vold.fstab"));
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                if (line.startsWith("dev_mount")) {
                    String[] lineElements = line.split(" ");
                    String element = lineElements[2];

                    if (element.contains(":"))
                        element = element.substring(0, element.indexOf(":"));

                    if (element.contains("usb"))
                        continue;

                    // don't add the default vold path
                    // it's already in the list.
                    if (!mounts.contains(element))
                        mounts.add(element);
                }
            }
        } catch (Exception e) {
            // swallow - don't care
            e.printStackTrace();
        }
    }

    private static void setProperties(Context sContext) {
        /*
		 * At this point all the paths in the list should be valid. Build the
		 * public properties.
		 */

        ArrayList<String> labelList = new ArrayList<String>();

        int j = 0;
        if (mounts.size() > 0) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD)
                labelList.add("Auto");
            else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                if (Environment.isExternalStorageRemovable()) {
                    labelList.add(sContext
                            .getString(R.string.tattobr_utils_text_external_storage) + " 1");
                    j = 1;
                } else
                    labelList.add(sContext
                            .getString(R.string.tattobr_utils_text_internal_storage));
            } else {
                if (!Environment.isExternalStorageRemovable()
                        || Environment.isExternalStorageEmulated())
                    labelList.add(sContext
                            .getString(R.string.tattobr_utils_text_internal_storage));
                else {
                    labelList.add(sContext
                            .getString(R.string.tattobr_utils_text_external_storage) + " 1");
                    j = 1;
                }
            }

            if (mounts.size() > 1) {
                for (int i = 1; i < mounts.size(); i++) {
                    labelList.add(sContext
                            .getString(R.string.tattobr_utils_text_external_storage)
                            + " " + (i + j));
                }
            }
        }

        labels = new String[labelList.size()];
        labelList.toArray(labels);

        paths = new String[mounts.size()];
        mounts.toArray(paths);

        count = Math.min(labels.length, paths.length);

		/*
		 * don't need these anymore, clear the lists to reduce memory use and to
		 * prepare them for the next time they're needed.
		 */
        mounts.clear();
    }

    private static void testAndCleanList(Context sContext) {
		/*
		 * Now that we have a cleaned list of mount paths, test each one to make
		 * sure it's a valid and available path. If it is not, remove it from
		 * the list.
		 */

        for (int i = 0; i < mounts.size(); i++) {
            String voldPath = mounts.get(i);
            File path = new File(voldPath);
            if (!path.exists() || !path.isDirectory() || !path.canWrite())
                mounts.remove(i--);
        }
    }
}
