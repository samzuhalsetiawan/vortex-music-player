package com.samzuhalsetiawan.localstorage.util

internal class MetadataToColumnNameNotMatchException(columnName: String) :
    Exception("$columnName does not have a corresponding column name in MediaStore")
