package cc.firebloom.sahara;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract of all content provider in this project
 * 
 * @author snow.hellsing@gmail.com
 *
 */
public final class SaharaContract {
	public static final String AUTHORITY = "cc.firebloom.sahara";
	
	// This class cannot be instantiated
    private SaharaContract() {
    }
    
    /**
     * keyword table contract
     */
    public static final class Keywords implements BaseColumns {
    	
    	// This class cannot be instantiated
        private Keywords() {}
        
        /*
         * URI definitions
         */

        /**
         * The scheme part for this provider's URI
         */
        private static final String SCHEME = "content://";

        /**
         * Path parts for the URIs
         */
        
        public static final String TABLE_NAME = "keywords";

        /**
         * Path part for the Keywords URI
         */
        private static final String PATH_KEYWORDS = "/" + TABLE_NAME;

        /**
         * Path part for the Keyword ID URI
         */
        private static final String PATH_KEYWORD_ID = "/"+ TABLE_NAME + "/";

        /**
         * 0-relative position of a note ID segment in the path part of a note ID URI
         */
        public static final int KEYWORD_ID_PATH_POSITION = 1;

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI =  Uri.parse(SCHEME + AUTHORITY + PATH_KEYWORDS);

        /**
         * The content URI base for a single note. Callers must
         * append a numeric note id to this Uri to retrieve a note
         */
        public static final Uri CONTENT_ID_URI_BASE
            = Uri.parse(SCHEME + AUTHORITY + PATH_KEYWORD_ID);

        /**
         * The content URI match pattern for a single note, specified by its ID. Use this to match
         * incoming URIs or to construct an Intent.
         */
        public static final Uri CONTENT_ID_URI_PATTERN
            = Uri.parse(SCHEME + AUTHORITY + PATH_KEYWORD_ID + "/#");

        /*
         * MIME type definitions
         */
        public static final String MIME = "vnd.cc.firebloom.sahara.keyword";

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + MIME;

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
         * note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + MIME;

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = _ID;

        /*
         * Column definitions
         */

        /**
         * Column name for the text of keyword
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_TEXT = "_text";

        /**
         * Column name for the creation timestamp
         * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
         */
        public static final String COLUMN_NAME_CREATE_DATE = "created";

        /**
         * Column name for the modification timestamp
         * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
         */
        public static final String COLUMN_NAME_MODIFICATION_DATE = "modified";
    }
}
