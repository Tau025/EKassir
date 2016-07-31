package com.devtau.ekassir.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.devtau.ekassir.model.Order;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
/**
 * Хелпер для операций работы с файловым хранилищем
 */
public class FileManager {
    private static final String LOG_TAG = FileManager.class.getSimpleName();

    @Nullable
    public static File getStoredVehiclePhoto(Context context, Order order) {
        String appDir = getFullAppDirectoryPath(context);
        String fileName = getPhotoFileName(order);
        File photoFile = new File(appDir, fileName);
        return checkFileExists(photoFile) ? photoFile : null;
    }

    public static void saveImageLocally(Context context, Order order, Bitmap bitmap) {
        //найдем на устройстве папку для фото по умолчанию
        String appDir = getFullAppDirectoryPath(context);
        OutputStream outputStream = null;

        try {
            //создадим новый файл для картинки
            File file = new File(appDir, getPhotoFileName(order));
            Logger.d(LOG_TAG, "Creating new file at: " + String.valueOf(file.getAbsolutePath()));
            outputStream = new FileOutputStream(file);

            //сожмем картинку в JPEG и сохраним ее в созданный выше файл
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            killFileWithDelay(file);
        } catch (IOException e) {
            Logger.e(LOG_TAG, "Error while handling FileOutputStream", e);
        } finally {
            try {
                if(outputStream != null) {
                    Logger.v(LOG_TAG, "Closing outputStream");
                    outputStream.close();
                }
            } catch (IOException e) {
                Logger.e(LOG_TAG, "Error while closing outputStream", e);
            }
        }
    }

    //метод нужен из-за того, что мы используем префикс для всех хранимых фотографий
    //и конвертируем их в jpg независимо от их формата на сервере
    private static String getPhotoFileName(Order order) {
        String fileName = order.getVehicle().getPhoto().substring(0, order.getVehicle().getPhoto().indexOf('.'));
        return Constants.VEHICLE_PHOTO_PREFIX + fileName + Constants.VEHICLE_PHOTO_FORMAT;
    }

    @NonNull
    private static String getFullAppDirectoryPath(Context context) {
        //найдем на устройстве папку для фото по умолчанию
        String baseDir = context.getApplicationInfo().dataDir;

        //и допишем к ней идентификатор папки нашего приложения
        File appDir = new File(baseDir, Constants.BASE_PHOTOS_DIR_NAME);
        if (!checkDirectoryExists(appDir)) {
            Logger.d(LOG_TAG, "Dir not found. Creating at: " + String.valueOf(appDir.getAbsolutePath()));
            appDir.mkdirs();
        }
        return appDir.getPath();
    }

    private static boolean checkDirectoryExists(File folder) {
        return folder.exists() && folder.isDirectory();
    }

    private static boolean checkFileExists(File file) {
        return file.exists() && file.isFile();
    }

    //определим срок жизни фотографии в кеше на устройстве
    private static void killFileWithDelay(final File file) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String fileName = file.getAbsolutePath();
                boolean isDeleted = file.delete();
                Logger.d(LOG_TAG, "file: " + fileName + (isDeleted ? " had been deleted" : " is alive"));
            }
        }, Constants.CASHED_PHOTO_LIFETIME);
    }
}
