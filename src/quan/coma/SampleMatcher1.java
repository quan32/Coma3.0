package quan.coma;

import java.util.ArrayList;
import java.util.HashSet;

import org.apache.log4j.Logger;

import de.wdilab.ml.impl.Similarity;
import de.wdilab.ml.impl.matcher.simple.AbstractSimpleAttributeObjectMatcher;
import de.wdilab.ml.impl.matcher.simple.trigram.StringSimilarity;
import de.wdilab.ml.impl.util.tokenizer.IStringTokenizer;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.matcher.IAttributeObjectMatcher;
import de.wdilab.ml.interfaces.oi.IObjectInstance;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;

public class SampleMatcher1 extends AbstractSimpleAttributeObjectMatcher
implements IAttributeObjectMatcher
{
	protected static final Logger log = Logger.getLogger(SampleMatcher1.class);
	private IStringTokenizer tokenizer;
	ArrayList<String> wordList = null; ArrayList<String> synonymList = null;
	private ArrayList<ArrayList<String>> synonymPairList = new ArrayList<ArrayList<String>>();
	ArrayList<String> abbrevList = null; ArrayList<String> fullFormList = null;
	private ArrayList<ArrayList<String>> abbrevPairList = new ArrayList<ArrayList<String>>();
	public static final String NGRAM_KEY = "ifuice_ngram_key";
	public static final String NULL = "<NULL>";
	protected static final String[] NULL_TRGS = new String[0];

	protected static final int[] NULL_IDS = new int[0];

	public void setTokenizer(IStringTokenizer tokenizer)
	{
		this.tokenizer = tokenizer;
	}

	public SampleMatcher1(String attr1, String attr2, IStringTokenizer tokenizer) {
		super(attr1, attr2, 0.0F);
		this.tokenizer = tokenizer;
	}

	public SampleMatcher1(String attr1, String attr2)
	{
		super(attr1, attr2, 0.0F);
		this.tokenizer = new ComaStringTokenizer1();
	}

	public SampleMatcher1(String attr)
	{
		super(attr, 0.0F);
		this.tokenizer = new ComaStringTokenizer1();
	}

	public SampleMatcher1()
	{
		this.tokenizer = new ComaStringTokenizer1();
	}

	public SampleMatcher1(String attr1, String attr2, float threshold)
	{
		super(attr1, attr2, threshold);
		this.tokenizer = new ComaStringTokenizer1();
	}

	public SampleMatcher1(String attr1, String attr2, float threshold, ArrayList<String> wordList, ArrayList<String> synonymList)
	{
		super(attr1, attr2, threshold);
		this.tokenizer = new ComaStringTokenizer1();
		if ((wordList != null) && (synonymList != null)) {
			this.wordList = wordList;
			this.synonymList = synonymList;

			for (int i = 0; i < wordList.size(); i++) {
				ArrayList<String> syn = new ArrayList<String>();
				syn.add((String)wordList.get(i));
				syn.add((String)synonymList.get(i));
				this.synonymPairList.add(syn);
			}
		}
	}
	
	public SampleMatcher1(String attr1, String attr2, float threshold, ArrayList<String> wordList, ArrayList<String> synonymList
			, ArrayList<String> abbrevList, ArrayList<String> fullFormList)
	{
		super(attr1, attr2, threshold);
		this.tokenizer = new ComaStringTokenizer1();
		
		if ((wordList != null) && (synonymList != null)) {
			this.wordList = wordList;
			this.synonymList = synonymList;

			for (int i = 0; i < wordList.size(); i++) {
				ArrayList<String> syn = new ArrayList<String>();
				syn.add((String)wordList.get(i));
				syn.add((String)synonymList.get(i));
				this.synonymPairList.add(syn);
			}
		}
		
		if ((abbrevList != null) && (fullFormList != null)) {
			this.abbrevList = abbrevList;
			this.fullFormList = fullFormList;

			for (int i = 0; i < abbrevList.size(); i++) {
				ArrayList<String> abbrev = new ArrayList<String>();
				abbrev.add((String)abbrevList.get(i));
				abbrev.add((String)fullFormList.get(i));
				this.abbrevPairList.add(abbrev);
			}
		}
	}

	public SampleMatcher1(String attr, float threshold)
	{
		super(attr, threshold);
		this.tokenizer = new ComaStringTokenizer1();
	}

	public void match(IObjectInstanceProvider oip1, IObjectInstanceProvider oip2, IMappingStore mrs)
			throws MappingStoreException
			{
		log.info("Match Start 1.");
		System.out.println("Threshold:"+this.threshold);
		
		//do something with left Object
		ArrayList<ArrayList<Object>> aComps = new ArrayList<ArrayList<Object>>();
		IObjectInstance[] idsLeft = new IObjectInstance[oip1.size()];
		ArrayList<String[]> leftnGrams = new ArrayList<String[]>();
		ArrayList<int[]> leftnGramsId = new ArrayList<int[]>();
		ArrayList<Object> leftStrings = new ArrayList<Object>();
		int left = 0;
		int ind = 0;
		String[] trgs;
		for (IObjectInstance oi : oip1) {
			idsLeft[(ind++)] = oi;
			String value = oi.getStringValue(this.attrLinks);
			
//			System.out.println("Original left:"+value);
			this.tokenizer.reset(value);
			String[] tokens = this.tokenizer.toArray();
			ArrayList<Object> a = new ArrayList<Object>();
			for (int i = 0; i < tokens.length; i++) {
				String string_left = tokens[i];
//				System.out.println("Token"+i+ ":"+string_left);
				if (string_left != null)
				{
					if (!leftStrings.contains(string_left)) {
						leftStrings.add(string_left);
						trgs = StringSimilarity.generateNGrams(
								string_left, 3);
						leftnGrams.add(trgs);
						leftnGramsId
						.add(StringSimilarity.generateNGramId(trgs));
						left++;
					}
					a.add(string_left);
				}
				else if (!leftStrings.contains("<NULL>")) {
					leftStrings.add("<NULL>");
					leftnGrams.add(NULL_TRGS);
					leftnGramsId.add(NULL_IDS);
					left++;
				}

			}

			aComps.add(a);
			
		}

		//do something with right object
		ArrayList<ArrayList<Object>> bComps = new ArrayList<ArrayList<Object>>();
		IObjectInstance[] idsRight = new IObjectInstance[oip2.size()];
		ArrayList<String[]> rightnGrams = new ArrayList<String[]>();
		ArrayList<int[]> rightnGramsId = new ArrayList<int[]>();
		ArrayList<Object> rightStrings = new ArrayList<Object>();
		int right = 0;
		ind = 0;
		for (IObjectInstance oi : oip2) {
			idsRight[(ind++)] = oi;
			String value = oi.getStringValue(this.attrRechts);
			
			
			this.tokenizer.reset(value);
			String[] tokens = this.tokenizer.toArray();
			ArrayList<Object> b = new ArrayList<Object>();
			for (int i = 0; i < tokens.length; i++) {
				String string_right = tokens[i];
				if (string_right != null)
				{
					if (!rightStrings.contains(string_right)) {
						rightStrings.add(string_right);
						String[] trgs1 = StringSimilarity.generateNGrams(
								string_right, 3);
						rightnGrams.add(trgs1);
						rightnGramsId.add(
								StringSimilarity.generateNGramId(trgs1));
						right++;
					}
					b.add(string_right);
				}
				else if (!rightStrings.contains("<NULL>")) {
					rightStrings.add("<NULL>");
					rightnGrams.add(NULL_TRGS);
					rightnGramsId.add(NULL_IDS);
					right++;
				}

			}

			bComps.add(b);
			
				
		}

		// compute similarity of elements of 2 objects
		int inputSize = leftStrings.size();
		int outputSize = rightStrings.size();
		float[][] simMatrix = new float[inputSize][outputSize];

		for (left = 0; left < inputSize; left++) {
			String left_string = (String)leftStrings.get(left);
			if (left_string != "<NULL>")
			{
				int[] leftnGramsIds = (int[])leftnGramsId.get(left);

				for (right = 0; right < outputSize; right++) {
					String right_string = (String)rightStrings.get(right);
					if (right_string != "<NULL>")
					{
						float sim = 0.0f;
						
						//if left and right are number
						if(isNumber(left_string) && isNumber(right_string)){
							
							float a= Float.parseFloat(left_string);
							float b=Float.parseFloat(right_string);
							float ab = a-b;
							ab = Math.abs(a-b);
							if(a==0)
								sim = 0;
							else{
								sim = 1.0f - (float)ab/a;
								if(sim < 0)
									sim =0;
							}
//							System.out.println("Chu y:"+left_string + " <-> "+ right_string+" : "+sim);  // Sample Output
						}else{
							//compute similarity of two element
							sim = StringSimilarity.computeNGramSimilarity(
									left_string, right_string, 3, leftnGramsIds, 
									(int[])rightnGramsId.get(right));
							
							//compute similarity base on synnonym list
							if (this.wordList != null) {
								float synonymSim = 0.0F;
								if (isDirectSynonym(left_string, right_string)) synonymSim = 1.0F;
								else if (isInheritedSynonym(left_string, right_string)) synonymSim = 0.8F;
								if (synonymSim > sim)
								{
									sim = synonymSim;
								}
							}
							
							//compute similarity base abbrev list
							if (this.abbrevList != null) {
								float abbrevSim = 0.0F;
								if (isDirectAbbrev(left_string, right_string)) abbrevSim = 1.0F;
								if (abbrevSim > sim)
								{
									sim = abbrevSim;
								}
							}
						}					
						
						//assign in to sim Array
						if (sim > this.threshold)
						{
							simMatrix[left][right] = sim;
						}else
							continue;
					}
				}
			}

		}
		
//		System.out.println("Array:"+simMatrix[0][0]);
		log.info("start 1");
		atomicToCompositeSimMatrix(leftStrings, rightStrings, simMatrix, 
				aComps, bComps, idsLeft, idsRight, mrs);
			}

	public void match(IObjectInstanceProvider oip, IMappingStore mrs)
			throws MappingStoreException
			{
//		log.info("Match Start. 2");
//
//		ArrayList aComps = new ArrayList();
//		IObjectInstance[] idsLeft = new IObjectInstance[oip.size()];
//		ArrayList leftnGrams = new ArrayList();
//		ArrayList leftnGramsId = new ArrayList();
//		ArrayList leftStrings = new ArrayList();
//		int left = 0;
//		int ind = 0;
//		String[] trgs;
//		for (IObjectInstance oi : oip) {
//			idsLeft[(ind++)] = oi;
//			String value = oi.getStringValue(this.attrLinks);
//			this.tokenizer.reset(value);
//			String[] tokens = this.tokenizer.toArray();
//			ArrayList a = new ArrayList();
//			for (int i = 0; i < tokens.length; i++) {
//				String string_left = tokens[i];
//				if (string_left != null)
//				{
//					leftStrings.add(string_left);
//					trgs = StringSimilarity.generateNGrams(
//							string_left, 3);
//					leftnGrams.add(trgs);
//					leftnGramsId.add(StringSimilarity.generateNGramId(trgs));
//					a.add(string_left);
//				} else {
//					leftStrings.add("<NULL>");
//					leftnGrams.add(NULL_TRGS);
//					leftnGramsId.add(NULL_IDS);
//				}
//				left++;
//			}
//			aComps.add(a);
//		}
//
//		ArrayList bComps = new ArrayList();
//		IObjectInstance[] idsRight = new IObjectInstance[oip.size()];
//		ArrayList rightnGrams = new ArrayList();
//		ArrayList rightnGramsId = new ArrayList();
//		ArrayList rightStrings = new ArrayList();
//		int right = 0;
//		ind = 0;
//		for (IObjectInstance oi : oip) {
//			idsRight[(ind++)] = oi;
//			String value = oi.getStringValue(this.attrRechts);
//			this.tokenizer.reset(value);
//			String[] tokens = this.tokenizer.toArray();
//			ArrayList b = new ArrayList();
//			for (int i = 0; i < tokens.length; i++) {
//				String string_right = tokens[i];
//				if (string_right != null)
//				{
//					rightStrings.add(string_right);
//					String[] trgs1 = StringSimilarity.generateNGrams(
//							string_right, 3);
//					rightnGrams.add(trgs1);
//					rightnGramsId.add(StringSimilarity.generateNGramId(trgs1));
//					b.add(string_right);
//				} else {
//					rightStrings.add("<NULL>");
//					rightnGrams.add(NULL_TRGS);
//					rightnGramsId.add(NULL_IDS);
//				}
//
//				right++;
//			}
//
//			bComps.add(b);
//		}
//
//		int inputSize = leftStrings.size();
//		int outputSize = rightStrings.size();
//		float[][] simMatrix = new float[inputSize][outputSize];
//
//		for (left = 0; left < inputSize; left++) {
//			String left_string = (String)leftStrings.get(left);
//			if (left_string != "<NULL>")
//			{
//				int[] leftnGramsIds = (int[])leftnGramsId.get(left);
//
//				for (right = 0; right < outputSize; right++) {
//					String right_string = (String)rightStrings.get(right);
//					if (right_string != "<NULL>")
//					{
//						float sim = StringSimilarity.computeNGramSimilarity(
//								left_string, right_string, 3, leftnGramsIds, 
//								(int[])rightnGramsId.get(right));
//
//						if (this.wordList != null) {
//							float synonymSim = 0.0F;
//							if (isDirectSynonym(left_string, right_string)) synonymSim = 1.0F;
//							else if (isInheritedSynonym(left_string, right_string)) synonymSim = 0.8F;
//							if (synonymSim > sim)
//							{
//								sim = synonymSim;
//							}
//						}
//						if (sim > 0.0F)
//						{
//							simMatrix[left][right] = sim;
//						}
//					}
//				}
//			}
//		}
//
//		log.info("start 2");
//		atomicToCompositeSimMatrix(leftStrings, rightStrings, simMatrix, 
//				aComps, bComps, idsLeft, idsRight, mrs);
			}

	public void match(IMappingProvider mp, IMappingStore mrs)
			throws MappingStoreException
			{
//		log.info("Match Start. 3");
//		HashSet oiLeft = new HashSet();
//		HashSet oiRight = new HashSet();
//		HashSet mids = new HashSet();
//		for (IMappingEntry e : mp) {
//			oiLeft.add(e.getLeft());
//			oiRight.add(e.getRight());
//			mids.add(
//					Integer.valueOf(e.getLeft().getId().hashCode() + 
//							e.getRight().getId().hashCode()));
//		}
//
//		ArrayList aComps = new ArrayList();
//		IObjectInstance[] idsLeft = new IObjectInstance[oiRight.size()];
//		ArrayList leftnGrams = new ArrayList();
//		ArrayList leftnGramsId = new ArrayList();
//		ArrayList leftStrings = new ArrayList();
//		int left = 0;
//		int ind = 0;
//		String[] trgs;
//		for (IObjectInstance oi : oiLeft) {
//			idsLeft[(ind++)] = oi;
//			String value = oi.getStringValue(this.attrLinks);
//			this.tokenizer.reset(value);
//			String[] tokens = this.tokenizer.toArray();
//			ArrayList a = new ArrayList();
//			for (int i = 0; i < tokens.length; i++) {
//				String string_left = tokens[i];
//				if (string_left != null)
//				{
//					leftStrings.add(string_left);
//					trgs = StringSimilarity.generateNGrams(
//							string_left, 3);
//					leftnGrams.add(trgs);
//					leftnGramsId.add(StringSimilarity.generateNGramId(trgs));
//					a.add(string_left);
//				} else {
//					leftStrings.add("<NULL>");
//					leftnGrams.add(NULL_TRGS);
//					leftnGramsId.add(NULL_IDS);
//				}
//				left++;
//			}
//			aComps.add(a);
//		}
//
//		ArrayList bComps = new ArrayList();
//		IObjectInstance[] idsRight = new IObjectInstance[oiRight.size()];
//		ArrayList rightnGrams = new ArrayList();
//		ArrayList rightnGramsId = new ArrayList();
//		ArrayList rightStrings = new ArrayList();
//		int right = 0;
//		ind = 0;
//		for (IObjectInstance oi : oiRight) {
//			idsRight[(ind++)] = oi;
//			String value = oi.getStringValue(this.attrRechts);
//			this.tokenizer.reset(value);
//			String[] tokens = this.tokenizer.toArray();
//			ArrayList b = new ArrayList();
//			for (int i = 0; i < tokens.length; i++) {
//				String string_right = tokens[i];
//				if (string_right != null)
//				{
//					rightStrings.add(string_right);
//					String[] trgs1 = StringSimilarity.generateNGrams(
//							string_right, 3);
//					rightnGrams.add(trgs1);
//					rightnGramsId.add(StringSimilarity.generateNGramId(trgs1));
//					b.add(string_right);
//				} else {
//					rightStrings.add("<NULL>");
//					rightnGrams.add(NULL_TRGS);
//					rightnGramsId.add(NULL_IDS);
//				}
//
//				right++;
//			}
//
//			bComps.add(b);
//		}
//
//		int inputSize = leftStrings.size();
//		int outputSize = rightStrings.size();
//		float[][] simMatrix = new float[inputSize][outputSize];
//
//		for (left = 0; left < inputSize; left++) {
//			String left_string = (String)leftStrings.get(left);
//			if (left_string != "<NULL>")
//			{
//				int[] leftnGramsIds = (int[])leftnGramsId.get(left);
//
//				for (right = 0; right < outputSize; right++) {
//					String right_string = (String)rightStrings.get(right);
//					if (right_string != "<NULL>")
//					{
//						float sim = StringSimilarity.computeNGramSimilarity(
//								left_string, right_string, 3, leftnGramsIds, 
//								(int[])rightnGramsId.get(right));
//
//						if (this.wordList != null) {
//							float synonymSim = 0.0F;
//							if (isDirectSynonym(left_string, right_string)) synonymSim = 1.0F;
//							else if (isInheritedSynonym(left_string, right_string)) synonymSim = 0.8F;
//							if (synonymSim > sim)
//							{
//								sim = synonymSim;
//							}
//						}
//						if (sim > 0.0F)
//						{
//							simMatrix[left][right] = sim;
//						}
//					}
//				}
//			}
//		}
//
//		log.info("start 3");
//		atomicToCompositeSimMatrix(leftStrings, rightStrings, simMatrix, 
//				aComps, bComps, idsLeft, idsRight, mrs, mids);
			}

	public void atomicToCompositeSimMatrix(ArrayList<Object> aAtoms, ArrayList<Object> bAtoms, float[][] atomSimMatrix, ArrayList<ArrayList<Object>> aComps, ArrayList<ArrayList<Object>> bComps, IObjectInstance[] aIds, IObjectInstance[] bIds, IMappingStore mrs, HashSet<Integer> mids)
	{
		if ((aAtoms == null) || (bAtoms == null) || (aComps == null) || 
				(bComps == null)) {
			return;
		}
		boolean verbose = false;

		int aAtomCnt = aAtoms.size();
		int bAtomCnt = bAtoms.size();
		int aCompCnt = aComps.size();
		int bCompCnt = bComps.size();

		if (verbose) {
			System.out.println("atomicToCompositeSimMatrix(): ");
		}

		long start = System.currentTimeMillis();
		int[][] aInd = new int[aCompCnt][];
		int[][] bInd = new int[bCompCnt][];
		for (int i = 0; i < aCompCnt; i++) {
			ArrayList<?> aComp = (ArrayList<?>)aComps.get(i);
			if (aComp == null) {
				aInd[i] = null;
			}
			else {
				int aSize = aComp.size();
				aInd[i] = new int[aSize];
				for (int j = 0; j < aSize; j++)
					aInd[i][j] = aAtoms.indexOf(aComp.get(j));
			}
		}
		for (int i = 0; i < bCompCnt; i++) {
			ArrayList<?> bComp = (ArrayList<?>)bComps.get(i);
			if (bComp == null) {
				bInd[i] = null;
			}
			else {
				int bSize = bComp.size();
				bInd[i] = new int[bSize];
				for (int j = 0; j < bSize; j++)
					bInd[i][j] = bAtoms.indexOf(bComp.get(j));
			}
		}
		long end = System.currentTimeMillis();
		if (verbose) {
			System.out.println("List: Indexing of " + aAtomCnt + "/" + aCompCnt + 
					"-" + bAtomCnt + "/" + bCompCnt + ": " + 
					(float)(end - start) / 1000.0F);
		}

		start = System.currentTimeMillis();
		for (int i = 0; i < aCompCnt; i++) {
			if (aInd[i] != null)
			{
				int aSize = aInd[i].length;
				for (int j = 0; j < bCompCnt; j++)
					if (bInd[j] != null)
					{
						int bSize = bInd[j].length;
						float[][] simMatrix = new float[aSize][bSize];
						for (int m = 0; m < aSize; m++) {
							int p = aInd[i][m];
							if (p != -1)
							{
								for (int n = 0; n < bSize; n++) {
									int q = bInd[j][n];
									if (q != -1)
									{
										simMatrix[m][n] = atomSimMatrix[p][q];
									}
								}
							}
						}
						float sim = computeSetSimilarity(simMatrix);
						IObjectInstance oiL = aIds[i];
						IObjectInstance oiR = bIds[j];

						if ((sim > this.threshold) && 
								(mids.contains(
										Integer.valueOf(oiL.getId().hashCode() + 
												oiR.getId().hashCode()))))
							try {
								mrs.add(oiL, oiR, new Similarity(sim));
							}
						catch (MappingStoreException e) {
							e.printStackTrace();
						}
					}
			}
		}
		end = System.currentTimeMillis();
		if (verbose)
			System.out.println("List: Combined sim: " + aCompCnt + "-" + 
					bCompCnt + ": " + (float)(end - start) / 1000.0F);
	}

	public void atomicToCompositeSimMatrix(ArrayList<Object> aAtoms, ArrayList<Object> bAtoms, float[][] atomSimMatrix, ArrayList<ArrayList<Object>> aComps, ArrayList<ArrayList<Object>> bComps, IObjectInstance[] aIds, IObjectInstance[] bIds, IMappingStore mrs)
	{
		if ((aAtoms == null) || (bAtoms == null) || (aComps == null) || 
				(bComps == null)) {
			return;
		}
		boolean verbose = false;

		int aAtomCnt = aAtoms.size();
		int bAtomCnt = bAtoms.size();
		int aCompCnt = aComps.size();
		int bCompCnt = bComps.size();

		if (verbose) {
			System.out.println("atomicToCompositeSimMatrix(): ");
		}

		long start = System.currentTimeMillis();
		int[][] aInd = new int[aCompCnt][];
		int[][] bInd = new int[bCompCnt][];
		for (int i = 0; i < aCompCnt; i++) {
			ArrayList<?> aComp = (ArrayList<?>)aComps.get(i);
			if (aComp == null) {
				aInd[i] = null;
			}
			else {
				int aSize = aComp.size();
				aInd[i] = new int[aSize];
				for (int j = 0; j < aSize; j++)
					aInd[i][j] = aAtoms.indexOf(aComp.get(j));
			}
		}
		for (int i = 0; i < bCompCnt; i++) {
			ArrayList<?> bComp = (ArrayList<?>)bComps.get(i);
			if (bComp == null) {
				bInd[i] = null;
			}
			else {
				int bSize = bComp.size();
				bInd[i] = new int[bSize];
				for (int j = 0; j < bSize; j++)
					bInd[i][j] = bAtoms.indexOf(bComp.get(j));
			}
		}
		long end = System.currentTimeMillis();
		if (verbose) {
			System.out.println("List: Indexing of " + aAtomCnt + "/" + aCompCnt + 
					"-" + bAtomCnt + "/" + bCompCnt + ": " + 
					(float)(end - start) / 1000.0F);
		}

		start = System.currentTimeMillis();
		for (int i = 0; i < aCompCnt; i++) {
			if (aInd[i] != null)
			{
				int aSize = aInd[i].length;
				for (int j = 0; j < bCompCnt; j++)
					if (bInd[j] != null)
					{
						int bSize = bInd[j].length;
						float[][] simMatrix = new float[aSize][bSize];
						for (int m = 0; m < aSize; m++) {
							int p = aInd[i][m];
							if (p != -1)
							{
								for (int n = 0; n < bSize; n++) {
									int q = bInd[j][n];
									if (q != -1)
									{
										simMatrix[m][n] = atomSimMatrix[p][q];
									}
								}
							}
						}
						float sim = computeSetSimilarity(simMatrix);

						this.numberOfComparisons += 1;
						if (sim > this.threshold)
							try
						{
								mrs.add(aIds[i], bIds[j], new Similarity(sim));
						}
						catch (MappingStoreException e)
						{
							e.printStackTrace();
						}
					}
			}
		}
		end = System.currentTimeMillis();
		if (verbose)
			System.out.println("List: Combined sim: " + aCompCnt + "-" + 
					bCompCnt + ": " + (float)(end - start) / 1000.0F);
	}

	public static float computeSetSimilarity(float[][] simMatrix)
	{
		if (simMatrix == null)
			return 0.0F;
		int m = simMatrix.length;
		if (m == 0)
			return 0.0F;
		int n = simMatrix[0].length;
		if (n == 0)
			return 0.0F;
		float sim = 0.0F;

		float maxSim_i = 0.0F; float maxSim_j = 0.0F;
		float sumSim_i = 0.0F; float sumSim_j = 0.0F;
		for (int i = 0; i < m; i++) {
			maxSim_i = 0.0F;
			for (int j = 0; j < n; j++)
				if (maxSim_i < simMatrix[i][j])
					maxSim_i = simMatrix[i][j];
			sumSim_i += maxSim_i;
		}
		for (int j = 0; j < n; j++) {
			maxSim_j = 0.0F;
			for (int i = 0; i < m; i++)
				if (maxSim_j < simMatrix[i][j])
					maxSim_j = simMatrix[i][j];
			sumSim_j += maxSim_j;
		}
		sim = (sumSim_i + sumSim_j) / (m + n);

		return sim;
	}

	boolean isDirectSynonym(String elem1, String elem2)
	{
		if ((elem1 == null) || (elem2 == null)) return false;
		ArrayList<?> synList = getDirectSynonyms(elem1);
		if (synList != null)
			for (int i = 0; i < synList.size(); i++) {
				String syn = (String)synList.get(i);
				if (syn.equalsIgnoreCase(elem2))
					return true;
			}
		return false;
	}
	boolean isDirectAbbrev(String elem1, String elem2)
	{
		if ((elem1 == null) || (elem2 == null)) return false;
		ArrayList<?> abbrevList = getDirectAbbrev(elem1);
		if (abbrevList != null)
			for (int i = 0; i < abbrevList.size(); i++) {
				String abbrev = (String)abbrevList.get(i);
				if (abbrev.equalsIgnoreCase(elem2))
					return true;
			}
		return false;
	}

	ArrayList<String> getDirectSynonyms(String elem)
	{
		if (elem == null) return null;
		ArrayList<String> synList = new ArrayList<String>();
		for (int i = 0; i < this.synonymPairList.size(); i++) {
			ArrayList<?> syn = (ArrayList<?>)this.synonymPairList.get(i);
			String word1 = (String)syn.get(0);
			String word2 = (String)syn.get(1);

			word1 = word1.replaceAll(" ", "");
			word2 = word2.replaceAll(" ", "");

			if (elem.equalsIgnoreCase(word1))
				synList.add(word2);
			else if (elem.equalsIgnoreCase(word2))
				synList.add(word1);
		}
		if (!synList.isEmpty())
			return synList;
		return null;
	}
	
	ArrayList<String> getDirectAbbrev(String elem)
	{
		if (elem == null) return null;
		ArrayList<String> abbrevList = new ArrayList<String>();
		for (int i = 0; i < this.abbrevPairList.size(); i++) {
			ArrayList<?> abbrev = (ArrayList<?>)this.abbrevPairList.get(i);
			String word1 = (String)abbrev.get(0);
			String word2 = (String)abbrev.get(1);

			word1 = word1.replaceAll(" ", "");
			word2 = word2.replaceAll(" ", "");

			if (elem.equalsIgnoreCase(word1))
				abbrevList.add(word2);
			else if (elem.equalsIgnoreCase(word2))
				abbrevList.add(word1);
		}
		if (!abbrevList.isEmpty())
			return abbrevList;
		return null;
	}

	boolean isInheritedSynonym(String elem1, String elem2)
	{
		if ((elem1 == null) || (elem2 == null))
			return false;
		ArrayList<?> synList = getInheritedSynonyms(elem1);
		if (synList != null)
			for (int i = 0; i < synList.size(); i++) {
				String syn = (String)synList.get(i);
				if (syn.equalsIgnoreCase(elem2))
					return true;
			}
		return false;
	}

	ArrayList<String> getInheritedSynonyms(String elem) {
		ArrayList<String> synList = new ArrayList<String>();
		getInheritedSynonyms(synList, elem);
		if (synList.contains(elem))
			synList.remove(elem);
		return synList;
	}

	void getInheritedSynonyms(ArrayList<String> synList, String elem) {
		if (elem == null) return;
		ArrayList<String> currentSynList = getDirectSynonyms(elem);
		if (currentSynList != null) {
			currentSynList.removeAll(synList);
			synList.addAll(currentSynList);
			for (int i = 0; i < currentSynList.size(); i++) {
				String syn = (String)currentSynList.get(i);
				getInheritedSynonyms(synList, syn);
			}
		}
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