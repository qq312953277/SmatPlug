package com.phicomm.smartplug.modules.data.local.file;

import android.content.Context;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by yun.wang
 * Date :2017/6/26
 * Description: ***
 * Version: 1.0.0
 */
public class FileDataRepository {
    private Context mContext;

    public FileDataRepository(Context context) {
        mContext = context;
    }

    public void saveStringToFile(String data, String path) {
        byte[] b = data.getBytes();
        File file = new File(path);
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public String getStringFromFile(String path) {
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        String content = null;
        File file = new File(path);
        if (!file.exists()) {
            return "";
        }
        byte[] bytes = new byte[1024];
        try {
            fis = new FileInputStream(file);
            bos = new ByteArrayOutputStream(1024);

            int length = 0;
            while ((length = fis.read(bytes)) != -1) {
                bos.write(bytes, 0, length);
            }
            content = new String(bos.toByteArray());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return content;
    }

    public void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            File to = new File(file.getAbsolutePath()
                    + System.currentTimeMillis());
            file.renameTo(to);
            to.delete();
        }
    }

    public boolean writeResponseBodyToDisk(File file, long size, InputStream byteInputStream) {
        try {
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = size;
                long fileSizeDownloaded = 0;

                inputStream = byteInputStream;
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
}
