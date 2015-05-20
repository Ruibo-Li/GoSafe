package DataMiner;

import java.io.*;
import java.util.*;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONObject;

/**
 * Created by szeyiu on 4/24/15.
 */
public class FrequentItems {
    //Assumption: Frequent item groups can be stored in memory.
    private String inputPath;
    private String outputPath="out.txt";
    private String delimiter =",";
    private int bucketSize=0;
    private int itemSize=0;
    private double threshold=0.7;//threshold to be frequent items.
    private double minConf = 0.7;//minimum confidence
    private List<ItemSet> frequentList=new ArrayList<ItemSet>();

    public FrequentItems(String inputPath, String delimiter, int threshold) throws IOException {
        WordID.load(inputPath);
        this.inputPath=inputPath;
        this.delimiter = delimiter;
        this.threshold=threshold;
        this.itemSize = WordID.size;
    }
    public FrequentItems(String inputPath) throws IOException {
        WordID.load(inputPath);
        this.inputPath=inputPath;
        this.itemSize = WordID.size;
    }
    public void setOutputPath(String path){
        outputPath=path;
    }
    public String getOutputPath() {
        return outputPath;
    }
    public void setDelimiter(String delimiter){
        this.delimiter = delimiter;
    }
    public String getDelimiter(){
        return delimiter;
    }
    public int bucketSize(){
        return bucketSize;
    }
    public int itemSize(){
        return itemSize;
    }
    public void setThreshold(double threshold){
        this.threshold=threshold;
    }
    public double getThreshold(){
        return threshold;
    }
    public void setMinConf(double minConf){
        this.minConf = minConf;
    }
    /**
     * main function to find frequent items
     * using A-priori algorithm.
     * @return this in order to cascade the operations.
     * @throws IOException
     */
    public FrequentItems findFrequentPairs() throws IOException {
        File infile = new File(inputPath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(infile)));

        //C1 contains all items and its frequency in the input file.
        int[] C1 = new int[itemSize];
        for(int i=0;i<C1.length;++i)    C1[i]=0;
        String bucket;
        String[] bucketSplit;
        bucket=reader.readLine();
        while(bucket!=null){
            bucketSize++;
            bucketSplit = WordID.split(bucket); //bucket.split(delimiter);
            for(int i=0;i<bucketSplit.length;++i){
                int id = WordID.getId(bucketSplit[i]);
                if(id >= 0 && id < C1.length){//is a number
                    //System.out.println("C1[id], id="+id);
                    C1[id]++;
                } else {
                    System.out.println("Warning OOV: "+bucket);
                }
            }
            bucket=reader.readLine();
        }
        double thresholdNum = bucketSize * threshold; //should strictly bigger than this.
        //L1 filters those items in C1 whose frequency is too low to be a part of frequent pair.
        List<Integer> L1 = new ArrayList<Integer>();
        for(int i=0;i<itemSize;++i){
            if(C1[i]>=thresholdNum)  {
                L1.add(i);
                ItemSet ig1 = new ItemSet(i);
                ig1.occurrence = C1[i];
                frequentList.add(ig1);
            }
        }

        //C2 is the set of pair candidates that could be frequent pairs
        HashMap<ItemSet,Integer> C2 = new HashMap<ItemSet,Integer>(L1.size()*L1.size());
        for(int i=0;i<L1.size();++i){
            for(int j=i+1;j<L1.size();++j){
                ItemSet ig;
                if(L1.get(i)<L1.get(j))
                    ig=new ItemSet(L1.get(i),L1.get(j));
                else
                    ig=new ItemSet(L1.get(j),L1.get(i));
                C2.put(ig, ig.occurrence);
            }
        }
        reader.close();
        //Read file second time to find real frequent pairs from
        //pair candidates C2.
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(infile)));

        while(true){
            //read each line
            bucket=reader.readLine();
            if(bucket==null)    break;
            bucketSplit = WordID.split(bucket);//bucket.split(delimiter);
            for(int i=0;i<bucketSplit.length;++i){
                int id = WordID.getId(bucketSplit[i]);
                if(id<0) continue;
                for(int j=i+1;j<bucketSplit.length;++j){
                    //every possible pairs in each line.
                    //if it appears in C2, count that pair in C2
                    int id2 = WordID.getId(bucketSplit[j]);
                    if(id2<0) continue;
                    List<Integer> bucktlst = new ArrayList<Integer>();
                    bucktlst.add(id);
                    bucktlst.add(id2);
                    Collections.sort(bucktlst);
                    ItemSet ig = new ItemSet(bucktlst);
                    if(C2.containsKey(ig)){//CONTAINS HAS BUG HERE!!! WE SHOULD USE OTHER DS!!
                        C2.put(ig,C2.get(ig)+1);
                    }
                }
            }
        }

        //find true frequent pairs from C2
        for(ItemSet ig:C2.keySet()){
            int occ=C2.get(ig);
            if(occ>=thresholdNum){
                ig.occurrence=occ;
                frequentList.add(ig);
            }
        }
        reader.close();
        return this;
    }

    /**
     * This function finds frequent items with any size,
     * return the max number of items in frequent items.
     * @return
     */
    public int findFrequentAll() throws IOException {
        int k=3;//start with finding 3-frequent items.
        if(frequentList.isEmpty())  return 0;//no action if empty
        while(true) {
            //System.out.println("K="+k);
            HashSet<ItemSet> km1Set = new HashSet<ItemSet>(frequentList);
            int begin = frequentList.size() - 1;
            //possible items in frequent k-items sets.
            Set<Integer> candidateItems = new HashSet<Integer>();
            for (; begin >= 0; --begin) {
                if (frequentList.get(begin).itemList.size() == k - 1) {
                    for (int item : frequentList.get(begin).itemList)
                        candidateItems.add(item);
                } else break;
            }
            begin++;//now begin is the first index of (k-1)-set
            HashMap<ItemSet, Integer> Ck = new HashMap<ItemSet, Integer>();
            //Construct Ck set which is the set of possible frequent k-items.
            for (int i = begin; i < frequentList.size(); ++i) {
                //km1Tuple is frequent (k-1)-items that we have known.
                List<Integer> km1Tuple = new ArrayList<Integer>(frequentList.get(i).itemList);
                //we are going to construct frequent k-item candidate from (k-1)-item.
                //System.out.println("Build from Lk-1:"+km1Tuple);
                List<Integer> kTuple;
                for (int item : candidateItems) {
                    kTuple = null;
                    for (int j = 0; j < km1Tuple.size(); ++j) {
                        if(item==km1Tuple.get(j)) break;
                        if (item < km1Tuple.get(j)) {
                            kTuple=new ArrayList<Integer>(km1Tuple);
                            kTuple.add(j, item);
                            break;
                        }
                        if(j==km1Tuple.size()-1){
                            kTuple=new ArrayList<Integer>(km1Tuple);
                            kTuple.add(j+1,item);
                            break;
                        }
                    }
                    if(kTuple==null)    continue;
                    ItemSet ig = new ItemSet(kTuple);
                    boolean isC = true;
                    //if kTuple is frequent, at least every subset of kTuple is frequent
                    for(int m=0;m<ig.itemList.size();++m){
                        List<Integer> testL = new ArrayList<Integer>(ig.itemList);
                        testL.remove(m);
                        ItemSet testG = new ItemSet(testL);
                        if(!km1Set.contains(testG)){
                            isC=false;
                            break;
                        }
                    }
                    if(isC) Ck.put(ig, ig.occurrence);
                }
            }
            //System.out.println("Filter by k-comb subset");
            File infile = new File(inputPath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(infile)));
            String bucket = "";// reader.readLine();
            String[] bucketSplit;
            while (bucket != null) {
                bucketSplit = WordID.split(bucket);
                List<Integer> row = new ArrayList<Integer>();
                for(int j=0; j<bucketSplit.length; ++j){
                    row.add(WordID.getId(bucketSplit[j]));
                }
                Collections.sort(row);
                for(ItemSet ckis: Ck.keySet()){
                    if(isSubset(ckis.itemList, row)){
                        //System.out.println("Hit");
                        Ck.put(ckis, Ck.get(ckis)+1);
                    }
                }
                bucket = reader.readLine();
            }
            reader.close();
            double thresholdNum =  bucketSize * threshold; //should strictly bigger than this.
            int count = 0;
            for (ItemSet g : Ck.keySet()) {
                int occ = Ck.get(g);
                if (occ >= thresholdNum) {
                    count++;
                    g.occurrence = occ;
                    frequentList.add(g);
                }
            }
            if (count < k + 1) {
                System.out.println("Finish finding frequent sets");
                return k;
            }
            k++;
        }
    }

    private boolean isSubset(List<Integer> sub, List<Integer> set){
        if(sub.size()>set.size()) return false;
        for(int e : sub){
            boolean eq = false;
            for(int ee: set){
                if(ee==e){
                    eq=true;
                    break;
                }
            }
            if(!eq) return false;
        }
        return true;
    }

    private void sortResult(){
        Collections.sort(frequentList,Collections.reverseOrder());
    }

    class Rule implements Comparable<Rule>{
        String rule;
        double conf;
        double supp;
        public Rule(String rule,double conf,double supp){
            this.rule = rule;
            this.conf = conf;
            this.supp = supp;
        }
        @Override
        public int compareTo(Rule other){
            if(this.conf - other.conf > 0){
                return 1;
            } else if(this.conf - other.conf < 0){
                return -1;
            } else {
                return 0;
            }
        }
    }
    private List<Rule> ruleList;
    
    private JSONArray JSONrules;
    
    public JSONArray getJsonrules() {
    	return JSONrules;
    }
    
    public void findRules() throws Exception{
    	JSONrules = new JSONArray();
        Map<ItemSet,ItemSet> frequentMap = new HashMap<ItemSet,ItemSet>();
        for(ItemSet s : frequentList){
            frequentMap.put(s,s);
        }
        ruleList = new ArrayList<Rule>();
        for(ItemSet is: frequentList){
            if(is.itemList.size()>1) continue;
            ItemSet singleSet = is;
            int singleId = singleSet.itemList.get(0);
            for(ItemSet leftSet: frequentList){
                if(leftSet.itemList.contains(singleId)) continue;
                List<Integer> leftRightList = new ArrayList<Integer>(leftSet.itemList);
                leftRightList.add(singleId);
                Collections.sort(leftRightList);
                ItemSet tmp = new ItemSet(leftRightList);
                if(frequentMap.containsKey(tmp)){
                    ItemSet lrSet = frequentMap.get(tmp);
                    //if(!lrSet.itemList.equals(leftRightList)) continue;
                    double conf = 1.0*lrSet.occurrence/leftSet.occurrence;
                    double supp = (double)(lrSet.occurrence)/bucketSize;
                    if(conf>=minConf){
                    	JSONObject jo = new JSONObject();
                    	JSONArray lhs = new JSONArray();
                        StringBuilder sb = new StringBuilder();
                        sb.append('[');
                        for(int i=0;i<leftSet.itemList.size();i++) {
                            int leftId = leftSet.itemList.get(i);                         
                            String leftStr = WordID.getWord(leftId);
                            lhs.put(leftStr);
                            sb.append(leftStr);
                            if(i<leftSet.itemList.size()-1)
                                sb.append(", ");
                            else
                                sb.append(']');
                            //sb.append(leftStr+",,, ");
                        }
                        sb.append("=> ");
                        sb.append('['+WordID.getWord(singleId)+"] (Conf="+ conf*100 + "%, Supp=" + supp + "%)");
                        ruleList.add(new Rule(sb.toString(), conf, supp));
                        jo.put("lhs", lhs);
                        jo.put("rhs", WordID.getWord(singleId));
                        jo.put("support", supp);
                        jo.put("confidence", conf);
                        JSONrules.put(jo);
                    }
                }
            }
        }
        Collections.sort(ruleList,Collections.reverseOrder());
    }


    /**
     * print frequent items on screen.
     * Every line is the list of frequent items followed by percentage.
     */
    public void print(){
        sortResult();
        for(ItemSet ig:frequentList){
            System.out.print("[");
            for(int i=0; i<ig.itemList.size(); ++i) {
                int item = ig.itemList.get(i);
                System.out.print(WordID.getWord(item));
                if(i<ig.itemList.size()-1){
                    System.out.print(", ");
                } else {
                    System.out.print("]");
                }
            }
            float perc = (float)ig.occurrence*100/bucketSize;
            System.out.print(", "+perc+"%\n");
        }
        System.out.println("************************RULES************************");
        for(Rule r: ruleList){
            System.out.println(r.rule);
        }
    }

    public void printfile() throws IOException {
        sortResult();
        File outfile = new File(outputPath);
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(outfile)));
        for(ItemSet ig:frequentList){
            writer.write("[");
            for(int i=0; i<ig.itemList.size(); ++i) {
                writer.write(WordID.getWord(ig.itemList.get(i)));
                if(i<ig.itemList.size()-1){
                    writer.write(", ");
                } else {
                    writer.write("], ");
                }
            }
            float perc = (float)(ig.occurrence*100)/bucketSize;
            writer.write(perc + "%\n");
        }
        writer.write("************************RULES************************\n");
        for(Rule r: ruleList){
            writer.write(r.rule + "\n");
        }
        writer.flush();
        writer.close();
    }
}
