package com.wb.nextgenlibrary.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Thread.UncaughtExceptionHandler;

import com.wb.nextgenlibrary.util.utils.F;
import com.wb.nextgenlibrary.util.utils.NextGenLogger;

import android.content.Context;
import android.content.Intent;


public class ErrorHandler {

    private static final String DIAGNOSTICS_FILE = "flixster.stacktrace";
    private static final String EMAIL_RECIPIENT = "android@flixster.com";
    private static final String EMAIL_SUBJECT = "Android Diagnostic Report v";
    private static final ErrorHandler INSTANCE = new ErrorHandler();

    private boolean hasChecked;

    private ErrorHandler() {
    }

    public static ErrorHandler instance() {
        return INSTANCE;
    }

    /** Set custom uncaught exception handler to collect diagnostic information */
    public void setDefaultUncaughtExceptionHandler(final Context appContext) {
        final UncaughtExceptionHandler osUeh = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                String diagnostics = buildDiagnosticMessage(thread, ex);
                NextGenLogger.e(F.TAG, diagnostics);

                /* Save to internal storage */
                FileOutputStream fos = null;
                try {
                    String filename = DIAGNOSTICS_FILE;
                    fos = appContext.openFileOutput(filename, Context.MODE_PRIVATE);
                    fos.write(diagnostics.getBytes());
                    NextGenLogger.i(F.TAG, "Diagnostics saved to " + filename);
                } catch (FileNotFoundException e) {
                    NextGenLogger.e(F.TAG, "FileNotFoundException", e);
                } catch (IOException e) {
                    NextGenLogger.e(F.TAG, "IOException", e);
                } finally {
                    if (fos != null)
                        try {
                            fos.close();
                        } catch (IOException e) {
                            NextGenLogger.e(F.TAG, "IOException", e);
                        }
                }

                /* Call "super" hander so we don't get ANR'd */
                osUeh.uncaughtException(thread, ex);
            }
        });
    }

    /** Check if the app has previously crashed, if so, an email Intent is returned */
    public Intent checkForAppCrash(Context appContext) {
        if (hasChecked) {
            return null;
        }
        hasChecked = true;
        Intent intent = null;
        String diagnostics = checkForDiagnosticMessage(appContext);
        if (diagnostics != null) {

            /* Delete diagnostics */
            appContext.deleteFile(DIAGNOSTICS_FILE);

            /* Build Intent */
            String subject = EMAIL_SUBJECT;
            String body = diagnostics ;
            intent = buildEmailIntent(EMAIL_RECIPIENT, subject, body);
        }
        return intent;
    }

    private static Intent buildEmailIntent(String recipient, String subject, String body) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { recipient });
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.setType("message/rfc822");
        return intent;
    }

    private static String checkForDiagnosticMessage(Context appContext) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            br = new BufferedReader(new InputStreamReader(appContext.openFileInput(DIAGNOSTICS_FILE)));
            NextGenLogger.i(F.TAG, "Diagnostics found");
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            NextGenLogger.i(F.TAG, "No diagnostics found");
        } catch (IOException e) {
            NextGenLogger.e(F.TAG, "IOException", e);
        } finally {
            if (br != null)
                try {
                    br.close();
                } catch (IOException e) {
                    NextGenLogger.e(F.TAG, "IOException", e);
                }
        }
        return null;
    }

    private static String buildDiagnosticMessage(Thread thread, Throwable ex) {
        StringBuilder sb = new StringBuilder();
        sb.append("Uncaught exception thrown on thread " + thread.getName());
        sb.append("\n");
        sb.append(buildStackTrace(ex));
        return sb.toString();
    }

    private static String buildStackTrace(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        Throwable currEx = ex;
        boolean isNestedEx = false;
        while (currEx != null) {
            if (isNestedEx) {
                sb.append("Caused by: ");
            }
            sb.append(currEx.toString());
            sb.append("\n");
            StackTraceElement[] stack = currEx.getStackTrace();
            for (StackTraceElement s : stack) {
                sb.append("\t");
                sb.append(s.toString());
                sb.append("\n");
            }
            currEx = currEx.getCause();
            isNestedEx = true;
        }
        return sb.toString();
    }
}
