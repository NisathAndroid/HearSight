package com.example.txtextrct

import com.googlecode.tesseract.android.TessBaseAPI

object Config {
    const val TESS_ENGINE = TessBaseAPI.OEM_LSTM_ONLY
    var ADD_NEW_LANG=""
    var DETECT_TEXT=""
    var TESS_LANG = "eng+hin+tam+kan+tel+mal$ADD_NEW_LANG"
    const val TESS_URL = "https://github.com/tesseract-ocr/tessdata/raw/main/eng.traineddata"
}