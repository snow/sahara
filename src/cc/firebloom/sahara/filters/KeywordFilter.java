package cc.firebloom.sahara.filters;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.yaml.snakeyaml.Yaml;

import android.content.Context;
import cc.firebloom.sahara.R;

public class KeywordFilter {
  private static final String PLACEHOLDER_REQ_LINK = "__REQ_LN__";
  private static final String REGEX_LINK = "[\\d-]{5,13}|http://[\\w\\d.]+";
  private static final String REGEX_ZHCN_PUNCT = "[“！？；。，…【】《》『』]+";
	
	static public String isSpam(String text, Context context){
	  text = text.replaceAll("\\s+", "").replaceAll("\\p{Punct}", "").
		            replaceAll(REGEX_ZHCN_PUNCT, "");
		
		for(String kw:getKeywords(context)){
		  Pattern p = preprocessKeyword(kw);
		  Matcher m = p.matcher(text);
			if(m.find()){
				return kw;
			}
		}
		
		return null;
	}
	
	static public ArrayList<String> getKeywords(Context context) {
	  ArrayList<String> keywords = new ArrayList<String>();
	  String cachePath = context.getCacheDir().getPath() + "/keywords.yml";
	  Yaml yaml = new Yaml();
	  
	  try {
      InputStream cache = new FileInputStream(cachePath);
      keywords = (ArrayList<String>)yaml.load(cache);
    } catch (FileNotFoundException e) {
      // no cache, build from bottom
      InputStream localKeywords = context.getResources().
          openRawResource(R.raw.init_keywords);
      keywords = (ArrayList<String>)yaml.load(localKeywords);
    }
    
    return keywords;
	}
	
	static private Pattern preprocessKeyword(String keyword){
	  return Pattern.compile(keyword.replace(PLACEHOLDER_REQ_LINK, REGEX_LINK));
	}
}
