package io.left.meshim.utilities;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Generic file extensions
 */

public class FileExtensions {
    public final static Set IMAGE = new HashSet<>(Arrays.asList("png", "jpg","svg"));
    public final static String[] DOCUMENTS = new String[] {"txt","xlsx", "xls", "doc",
            "docx", "ppt", "pptx", "pdf","zip","gif"};
}
