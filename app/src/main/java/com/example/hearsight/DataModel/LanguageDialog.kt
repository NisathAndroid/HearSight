package com.example.hearsight.DataModel

import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hearsight.R
import com.example.txtextrct.Assets

class LanguageDialog(private val context:Context) {
    private lateinit var  dialog: Dialog
    lateinit var language_Recyclerview:RecyclerView
    var languageChooserDataList=ArrayList<LanguageChooserDataClass>()
    lateinit var languageAdapter: LanguageAdapter
    var sharedperefernce= SharedPreferenceBase(context)
    companion object var isLanguageChoosed=false
    fun SetDialog(): Dialog {
        dialog = Dialog(context)
        dialog.setContentView(R.layout.fragment_language_chooser)
        dialog.window!!.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
        language_Recyclerview=dialog.findViewById(R.id.language_recyclerview_id)
        setTesseractLangAdapter()
        setTexttoSpeechAdapter()
        dialog.show()
        return dialog
    }
    fun setTesseractLangAdapter() {
        languageChooserDataList.add(LanguageChooserDataClass("en","English","eng"))
        languageChooserDataList.add(LanguageChooserDataClass("hi", "Hindi", "hin"))
        languageChooserDataList.add(LanguageChooserDataClass("bn", "Bengali", "ben"))
        languageChooserDataList.add(LanguageChooserDataClass("te", "Telugu", "tel"))
        languageChooserDataList.add(LanguageChooserDataClass("mr", "Marathi", "mar"))
        languageChooserDataList.add(LanguageChooserDataClass("ta", "Tamil", "tam"))
        languageChooserDataList.add(LanguageChooserDataClass("ur", "Urdu", "urd"))
        languageChooserDataList.add(LanguageChooserDataClass("gu", "Gujarati", "guj"))
        languageChooserDataList.add(LanguageChooserDataClass("kn", "Kannada", "kan"))
        languageChooserDataList.add(LanguageChooserDataClass("ml", "Malayalam", "mal"))
        languageChooserDataList.add(LanguageChooserDataClass("pa", "Punjabi", "pan"))

        languageChooserDataList.add(LanguageChooserDataClass("af", "Afrikaans", "afr"))
        languageChooserDataList.add(LanguageChooserDataClass("sq", "Albanian", "sqi"))
        languageChooserDataList.add(LanguageChooserDataClass("am", "Amharic", "amh"))
        languageChooserDataList.add(LanguageChooserDataClass("ar", "Arabic", "ara"))
        languageChooserDataList.add(LanguageChooserDataClass("hy", "Armenian", "hye"))
        languageChooserDataList.add(LanguageChooserDataClass("as", "Assamese", "asm"))
        languageChooserDataList.add(LanguageChooserDataClass("az", "Azerbaijani", "aze"))
        languageChooserDataList.add(LanguageChooserDataClass("be", "Belarusian", "bel"))
        languageChooserDataList.add(LanguageChooserDataClass("bs", "Bosnian", "bos"))
        languageChooserDataList.add(LanguageChooserDataClass("bg", "Bulgarian", "bul"))
        languageChooserDataList.add(LanguageChooserDataClass("my", "Burmese", "mya"))
        languageChooserDataList.add(LanguageChooserDataClass("ca", "Catalan", "cat"))
        languageChooserDataList.add(LanguageChooserDataClass("ceb", "Cebuano", "ceb"))
        languageChooserDataList.add(LanguageChooserDataClass("chr", "Cherokee", "chr"))
        languageChooserDataList.add(LanguageChooserDataClass("zh-CN", "Chinese (Simplified)", "chi_sim"))
        languageChooserDataList.add(LanguageChooserDataClass("zh-TW", "Chinese (Traditional)", "chi_tra"))
        languageChooserDataList.add(LanguageChooserDataClass("hr", "Croatian", "hrv"))
        languageChooserDataList.add(LanguageChooserDataClass("cs", "Czech", "ces"))
        languageChooserDataList.add(LanguageChooserDataClass("da", "Danish", "dan"))
        languageChooserDataList.add(LanguageChooserDataClass("dv", "Dhivehi", "div"))
        languageChooserDataList.add(LanguageChooserDataClass("nl", "Dutch", "nld"))
        languageChooserDataList.add(LanguageChooserDataClass("en", "English", "eng"))
        languageChooserDataList.add(LanguageChooserDataClass("eo", "Esperanto", "epo"))
        languageChooserDataList.add(LanguageChooserDataClass("et", "Estonian", "est"))
        languageChooserDataList.add(LanguageChooserDataClass("fi", "Finnish", "fin"))
        languageChooserDataList.add(LanguageChooserDataClass("fr", "French", "fra"))
        languageChooserDataList.add(LanguageChooserDataClass("gl", "Galician", "glg"))
        languageChooserDataList.add(LanguageChooserDataClass("ka", "Georgian", "kat"))
        languageChooserDataList.add(LanguageChooserDataClass("de", "German", "deu"))
        languageChooserDataList.add(LanguageChooserDataClass("el", "Greek", "ell"))
        languageChooserDataList.add(LanguageChooserDataClass("gu", "Gujarati", "guj"))
        languageChooserDataList.add(LanguageChooserDataClass("ht", "Haitian Creole", "hat"))
        languageChooserDataList.add(LanguageChooserDataClass("he", "Hebrew", "heb"))
        languageChooserDataList.add(LanguageChooserDataClass("hi", "Hindi", "hin"))
        languageChooserDataList.add(LanguageChooserDataClass("hu", "Hungarian", "hun"))
        languageChooserDataList.add(LanguageChooserDataClass("is", "Icelandic", "isl"))
        languageChooserDataList.add(LanguageChooserDataClass("id", "Indonesian", "ind"))
        languageChooserDataList.add(LanguageChooserDataClass("ga", "Irish", "gle"))
        languageChooserDataList.add(LanguageChooserDataClass("it", "Italian", "ita"))
        languageChooserDataList.add(LanguageChooserDataClass("ja", "Japanese", "jpn"))
        languageChooserDataList.add(LanguageChooserDataClass("jv", "Javanese", "jav"))
        languageChooserDataList.add(LanguageChooserDataClass("kn", "Kannada", "kan"))
        languageChooserDataList.add(LanguageChooserDataClass("kk", "Kazakh", "kaz"))
        languageChooserDataList.add(LanguageChooserDataClass("km", "Khmer", "khm"))
        languageChooserDataList.add(LanguageChooserDataClass("ko", "Korean", "kor"))
        languageChooserDataList.add(LanguageChooserDataClass("ku", "Kurdish (Kurmanji)", "kur"))
        languageChooserDataList.add(LanguageChooserDataClass("ky", "Kyrgyz", "kir"))
        languageChooserDataList.add(LanguageChooserDataClass("lo", "Lao", "lao"))
        languageChooserDataList.add(LanguageChooserDataClass("la", "Latin", "lat"))
        languageChooserDataList.add(LanguageChooserDataClass("lv", "Latvian", "lav"))
        languageChooserDataList.add(LanguageChooserDataClass("lt", "Lithuanian", "lit"))
        languageChooserDataList.add(LanguageChooserDataClass("lb", "Luxembourgish", "ltz"))
        languageChooserDataList.add(LanguageChooserDataClass("mk", "Macedonian", "mkd"))
        languageChooserDataList.add(LanguageChooserDataClass("mg", "Malagasy", "mlg"))
        languageChooserDataList.add(LanguageChooserDataClass("ms", "Malay", "msa"))
        languageChooserDataList.add(LanguageChooserDataClass("ml", "Malayalam", "mal"))
        languageChooserDataList.add(LanguageChooserDataClass("mt", "Maltese", "mlt"))
        languageChooserDataList.add(LanguageChooserDataClass("mi", "Maori", "mri"))
        languageChooserDataList.add(LanguageChooserDataClass("mr", "Marathi", "mar"))
        languageChooserDataList.add(LanguageChooserDataClass("mn", "Mongolian", "mon"))
        languageChooserDataList.add(LanguageChooserDataClass("ne", "Nepali", "nep"))
        languageChooserDataList.add(LanguageChooserDataClass("no", "Norwegian", "nor"))
        languageChooserDataList.add(LanguageChooserDataClass("or", "Oriya", "ori"))
        languageChooserDataList.add(LanguageChooserDataClass("ps", "Pashto", "pus"))
        languageChooserDataList.add(LanguageChooserDataClass("fa", "Persian", "fas"))
        languageChooserDataList.add(LanguageChooserDataClass("pl", "Polish", "pol"))
        languageChooserDataList.add(LanguageChooserDataClass("pt", "Portuguese", "por"))
        languageChooserDataList.add(LanguageChooserDataClass("pa", "Punjabi", "pan"))
        languageChooserDataList.add(LanguageChooserDataClass("qu", "Quechua", "que"))
        languageChooserDataList.add(LanguageChooserDataClass("ro", "Romanian", "ron"))
        languageChooserDataList.add(LanguageChooserDataClass("ru", "Russian", "rus"))
        languageChooserDataList.add(LanguageChooserDataClass("sm", "Sami", "smi"))
        languageChooserDataList.add(LanguageChooserDataClass("sa", "Sanskrit", "san"))
        languageChooserDataList.add(LanguageChooserDataClass("gd", "Scots Gaelic", "gla"))
        languageChooserDataList.add(LanguageChooserDataClass("sr", "Serbian", "srp"))
        languageChooserDataList.add(LanguageChooserDataClass("st", "Sesotho", "sot"))
        languageChooserDataList.add(LanguageChooserDataClass("si", "Sinhalese", "sin"))
        languageChooserDataList.add(LanguageChooserDataClass("sk", "Slovak", "slk"))
        languageChooserDataList.add(LanguageChooserDataClass("sl", "Slovenian", "slv"))
        languageChooserDataList.add(LanguageChooserDataClass("es", "Spanish", "spa"))
        languageChooserDataList.add(LanguageChooserDataClass("su", "Sundanese", "sun"))
        languageChooserDataList.add(LanguageChooserDataClass("sw", "Swahili", "swa"))
        languageChooserDataList.add(LanguageChooserDataClass("sv", "Swedish", "swe"))
        languageChooserDataList.add(LanguageChooserDataClass("syr", "Syriac", "syr"))
        languageChooserDataList.add(LanguageChooserDataClass("tl", "Tagalog", "tgl"))
        languageChooserDataList.add(LanguageChooserDataClass("tg", "Tajik", "tgk"))
        languageChooserDataList.add(LanguageChooserDataClass("ta", "Tamil", "tam"))
        languageChooserDataList.add(LanguageChooserDataClass("tt", "Tatar", "tat"))
        languageChooserDataList.add(LanguageChooserDataClass("te", "Telugu", "tel"))
        languageChooserDataList.add(LanguageChooserDataClass("th", "Thai", "tha"))
        languageChooserDataList.add(LanguageChooserDataClass("bo", "Tibetan", "bod"))
        languageChooserDataList.add(LanguageChooserDataClass("tr", "Turkish", "tur"))
        languageChooserDataList.add(LanguageChooserDataClass("ug", "Uighur", "uig"))
        languageChooserDataList.add(LanguageChooserDataClass("uk", "Ukrainian", "ukr"))
        languageChooserDataList.add(LanguageChooserDataClass("ur", "Urdu", "urd"))
        languageChooserDataList.add(LanguageChooserDataClass("uz", "Uzbek", "uzb"))
        languageChooserDataList.add(LanguageChooserDataClass("vi", "Vietnamese", "vie"))
        languageChooserDataList.add(LanguageChooserDataClass("cy", "Welsh", "cym"))
        languageChooserDataList.add(LanguageChooserDataClass("xh", "Xhosa", "xho"))
        languageChooserDataList.add(LanguageChooserDataClass("yi", "Yiddish", "yid"))
        languageChooserDataList.add(LanguageChooserDataClass("yo", "Yoruba", "yor"))
        languageChooserDataList.add(LanguageChooserDataClass("zu", "Zulu", "zul"))
        val actualList=removeDuplicates(languageChooserDataList)
        languageAdapter= LanguageAdapter(context,actualList,object :LanguageAdapter.OnClickListener{
            override fun onitemclicklistener(tts_lang_code: String, tes_lang_code: String) {
                sharedperefernce.saveData("TTS_lang_code",tts_lang_code)
                sharedperefernce.saveData("TES_lang_code",tes_lang_code)
                dialog.dismiss()
                var resultTxt=""
                Assets.download_TrainedLanguage(context,tes_lang_code)
                sharedperefernce.saveData("result_text",resultTxt)
                isLanguageChoosed=true
            }
        })
        val manager= LinearLayoutManager(context)
        language_Recyclerview.layoutManager=manager
        language_Recyclerview.adapter=languageAdapter
        language_Recyclerview.invalidate()
    }

    fun setTexttoSpeechAdapter() {
        languageChooserDataList.add(LanguageChooserDataClass("en","English","eng"))
        languageChooserDataList.add(LanguageChooserDataClass("hi", "Hindi", "hin"))
        languageChooserDataList.add(LanguageChooserDataClass("bn", "Bengali", "ben"))
        languageChooserDataList.add(LanguageChooserDataClass("te", "Telugu", "tel"))
        languageChooserDataList.add(LanguageChooserDataClass("mr", "Marathi", "mar"))
        languageChooserDataList.add(LanguageChooserDataClass("ta", "Tamil", "tam"))
        languageChooserDataList.add(LanguageChooserDataClass("ur", "Urdu", "urd"))
        languageChooserDataList.add(LanguageChooserDataClass("gu", "Gujarati", "guj"))
        languageChooserDataList.add(LanguageChooserDataClass("kn", "Kannada", "kan"))
        languageChooserDataList.add(LanguageChooserDataClass("ml", "Malayalam", "mal"))
        languageChooserDataList.add(LanguageChooserDataClass("pa", "Punjabi", "pan"))

        val actualList=removeDuplicates(languageChooserDataList)
        languageAdapter= LanguageAdapter(context,actualList,object :LanguageAdapter.OnClickListener{
            override fun onitemclicklistener(tts_lang_code: String, tes_lang_code: String) {
                sharedperefernce.saveData("TTS_lang_code",tts_lang_code)
                Toast.makeText(context, "Speak language choosed", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        })
        val manager= LinearLayoutManager(context)
        language_Recyclerview.layoutManager=manager
        language_Recyclerview.adapter=languageAdapter
        language_Recyclerview.invalidate()
    }

    fun <T> removeDuplicates(list: ArrayList<T>): ArrayList<T> {
        val result = ArrayList<T>()
        val seen = HashSet<T>()
        for (item in list) {
            if (seen.add(item)) {
                result.add(item)
            }
        }
        return result
    }

}