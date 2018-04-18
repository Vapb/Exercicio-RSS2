package br.ufpe.cin.if1001.rss.db;

import android.provider.BaseColumns;

/**
 *.
 */

// interface Base columns ajuda com id e count
// public pra acessar na main
public class RssProviderContract implements BaseColumns  {
    //public static final String _ID = "_id";
    public static final String TITLE = "title";
    public static final String DATE = "date";
    public static final String DESCRIPTION = "description";
    public static final String LINK = "link";
    public static final String UNREAD = "unread";
}

