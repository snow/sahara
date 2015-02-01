package cc.firebloom.sahara;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;

public class Sender {
    private static final String PUBLIC_LIST_FILE = "public_list";
    private static final String CUSTOM_LIST_FILE = "custom_list";
    private static Sender sInst;

    private Set<String> mCustomList;
    private Set<String> mPublicList;

    private Sender() {
    }

    public static Sender getInst() {
        if (null == sInst) {
            sInst = new Sender();
        }
        return sInst;
    }

    public static String sanitizePhoneNumber(String num) {
        num = num.replaceAll("[^+0-9]", "");
        if (num.startsWith("+")) {
            return num;
        } else if (num.startsWith("86")) {
            return "+" + num;
        } else {
            return "+86" + num; // it's for China only for now
        }
    }

    public boolean shouldBlockNumber(String number) {
        number = number.replaceAll("\\D", "");

        // FIXME: should detect country code instead of use endsWith()
        for (String blockedNum : customList()) {
            if (number.equals(blockedNum)) {
                return true;
            }
        }

        for (String blockedNum : publicList()) {
            if (number.equals(blockedNum)) {
                return true;
            }
        }

        return false;
    }

    public Set<String> publicList() {
        if (null == mPublicList) {
            mPublicList = new HashSet<>();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(SaharaApplication
                    .getInstance().openFileInput(PUBLIC_LIST_FILE)))) {
                for (String line; null != (line = br.readLine()); ) {
                    mPublicList.add(sanitizePhoneNumber(line));
                }
            } catch (FileNotFoundException e) {
                loadDefaultPublicList();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return mPublicList;
    }

    private void loadDefaultPublicList() {
        mPublicList.clear();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(SaharaApplication
                .getInstance().getResources().openRawResource(R.raw.sender_black_list)))) {
            for (String line; null != (line = br.readLine()); ) {
                mPublicList.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> customList() {
        if (null == mCustomList) {
            mCustomList = new HashSet<>();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(SaharaApplication
                    .getInstance().openFileInput(CUSTOM_LIST_FILE)))) {
                for (String line; null != (line = br.readLine()); ) {
                    mCustomList.add(sanitizePhoneNumber(line));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return mCustomList;
    }

    public void addNumber(String number) {
        mCustomList.add(number);
        persistCustomList();
    }

    public void removeNumber(String number) {
        mCustomList.remove(number);
        persistCustomList();
    }

    private void persistCustomList() {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(SaharaApplication
                    .getInstance().openFileOutput(CUSTOM_LIST_FILE, Context.MODE_PRIVATE)));
            for (String s : mCustomList) {
                writer.write(s);
                writer.newLine();
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
