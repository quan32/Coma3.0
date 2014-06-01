package quan.coma;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import de.wdilab.ml.impl.util.tokenizer.AbstractStringTokenizer;

public class ComaStringTokenizer1 extends AbstractStringTokenizer
{
	ArrayList<String> tokenList;
	Iterator<String> it;

	  public void reset(String str)
	  {
		  this.tokenList = new ArrayList<String>();
		  
		  if ((str != null) && (str.length() > 0)) {
	    	String delimiters = " ";
	
	    	StringTokenizer st = new StringTokenizer(str, delimiters);
	
	      while (st.hasMoreTokens()) {
	        String elem = st.nextToken();
	        this.tokenList.add(elem);
	      }
	    }
	    this.it = this.tokenList.iterator();
	  }

//	public void reset(String str)
//	{
//		this.tokenList = new ArrayList<String>();
//		if(isNumber(str)){
//			this.tokenList.add(str);
//		}else{
//
//
//			if ((str != null) && (str.length() > 0))
//			{
//				String delimiters = " _-";
//
//
//				StringTokenizer st = new StringTokenizer(str, delimiters);
//				while (st.hasMoreTokens())
//				{
//					String elem = st.nextToken();
//					int elemLen = elem.length();
//
//
//					int upperCount = 0;
//					int[] charType = new int[elemLen];
//					for (int i = 0; i < elemLen; i++)
//					{
//						char c = elem.charAt(i);
//						if (Character.isUpperCase(c))
//						{
//							upperCount++;
//							charType[i] = 1;
//						}
//						else if (Character.isDigit(c))
//						{
//							charType[i] = 2;
//						}
//						else
//						{
//							charType[i] = 0;
//						}
//					}
//					if (upperCount > elemLen / 2)
//					{
//						this.tokenList.add(elem.toLowerCase());
//					}
//					else
//					{
//						int begin = 0;
//						int lastState = charType[0];
//						for (int i = 0; i < charType.length; i++) {
//							if (lastState != charType[i])
//							{
//								if (lastState == 1)
//								{
//									if (charType[i] == 2)
//									{
//										String token = elem.substring(begin, i).toLowerCase();
//										if (!this.tokenList.contains(token)) {
//											this.tokenList.add(token);
//										}
//										begin = i;
//									}
//									else if (i - begin > 1)
//									{
//										String token = elem.substring(begin, i - 1).toLowerCase();
//										if (!this.tokenList.contains(token)) {
//											this.tokenList.add(token);
//										}
//										begin = i - 1;
//										i--;
//									}
//								}
//								else if (lastState == 0)
//								{
//									String token = elem.substring(begin, i).toLowerCase();
//									if (!this.tokenList.contains(token)) {
//										this.tokenList.add(token);
//									}
//									begin = i;
//								}
//								else
//								{
//									String token = elem.substring(begin, i).toLowerCase();
//									if (!this.tokenList.contains(token)) {
//										this.tokenList.add(token);
//									}
//									begin = i;
//								}
//								lastState = charType[i];
//							}
//						}
//						String lastToken = elem.substring(begin, elem.length()).toLowerCase();
//						if (!this.tokenList.contains(lastToken)) {
//							this.tokenList.add(lastToken);
//						}
//					}
//				}
//			}
//		}
//		this.it = this.tokenList.iterator();
//	}

	public boolean hasMoreTokens()
	{
		if (this.it == null) return false;
		return this.it.hasNext();
	}

	public String nextToken()
	{
		return (String)this.it.next();
	}

	public static boolean isNumber(String s) {
		try { 
			Float.parseFloat(s); 
		} catch(NumberFormatException e) { 
			return false; 
		}
		// only got here if we didn't return false
		return true;
	}


}