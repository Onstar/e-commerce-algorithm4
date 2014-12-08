package e_commerce.algorithm4;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import e_commerce.algorithm4.stastic.ISequentialStastic;
import e_commerce.algorithm4.stastic.SequentialForSection;
import e_commerce.algorithm4.App;

/**
 * Hello world!
 *
 */
public class App {
	private static final Logger logger = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) throws IOException {

		if (args.length != 1) {
			logger.error("params:file path required!\r\n");
			return;
		}

		String filePath = args[0];
		logger.info("----------------------------------------\r\n");
		logger.info("start scanning {} ...\r\n", filePath);
		logger.info("----------------------------------------\r\n");
		InputStreamReader read = new InputStreamReader(new FileInputStream(
				filePath), "UTF-8");
		BufferedReader bufferedReader = new BufferedReader(read);
		String lineTxt = null;
		List<List<TrueAndFalse>> totalResult = new ArrayList<List<TrueAndFalse>>();
		int number = 1;
		while ((lineTxt = bufferedReader.readLine()) != null) {

			String source = lineTxt.trim();
			if (source.length() == 0) {
				logger.info("skip row : {}\r\n", source);
				continue;
			}

			SourceRow sRow = new SourceRow(source);
			logger.debug("{}. ", number++);
			sRow.print();
			IRow row = sRow.run();
			List<TrueAndFalse> rtn = row.run();
			for(TrueAndFalse taf : rtn){
				taf.print();
				taf.run();
			}
			totalResult.add(rtn);
		}
		bufferedReader.close();
		
		logger.info("--------------------------------------------------\r\n");
		logger.info("---------------------整个文件汇总-------------------\r\n");
		//for(int i=0; i<2; i++){//每一段的汇总
		int i=0;
			int max10=0, max15=0, max20=0;
			Map<Integer, Integer> mapCountOfMax10 = new TreeMap<Integer, Integer>();
			Map<Integer, Integer> mapCountOfMax15 = new TreeMap<Integer, Integer>();
			Map<Integer, Integer> mapCountOfMax20 = new TreeMap<Integer, Integer>();
			int sum = 0;
			int countTrue = 0, countFalse = 0;
			for(List<TrueAndFalse> list : totalResult)
				if(list.size() > i){
					
					TrueAndFalse taf = list.get(i);
					
					sum += taf.getSum();
					countTrue += taf.getCountTrue();
					countFalse += taf.getCountFalse();
					
					int tmpMax10 = taf.getMax().get(10);
					int tmpMax15 = taf.getMax().get(15);
					int tmpMax20 = taf.getMax().get(20);
					max10 = max10>tmpMax10 ? max10 : tmpMax10;
					if(mapCountOfMax10.get(tmpMax10) == null)
						mapCountOfMax10.put(tmpMax10, 1);
					else
						mapCountOfMax10.put(tmpMax10, mapCountOfMax10.get(tmpMax10)+1);
					
					max15 = max15>tmpMax15 ? max15 : tmpMax15;
					if(mapCountOfMax15.get(tmpMax15) == null)
						mapCountOfMax15.put(tmpMax15, 1);
					else
						mapCountOfMax15.put(tmpMax15, mapCountOfMax15.get(tmpMax15)+1);
					
					max20 = max20>tmpMax20 ? max20 : tmpMax20;
					if(mapCountOfMax20.get(tmpMax20) == null)
						mapCountOfMax20.put(tmpMax20, 1);
					else
						mapCountOfMax20.put(tmpMax20, mapCountOfMax20.get(tmpMax20)+1);
				}
			
			logger.info("第{}段 \r\n\t[ SUM:{}, MAX(10):{}, MAX(15):{}, MAX(20):{}, x:{}({}%), o:{}({}%) ]\r\n", i+1, sum, 
					max10, max15, max20,
					countFalse, (float)countFalse*100/(float)(countFalse+countTrue),
					countTrue, (float)countTrue*100/(float)(countFalse+countTrue));
			
			logger.info("\tSUM>=10[ ");
			for(Map.Entry<Integer, Integer> entry : mapCountOfMax10.entrySet()){
				logger.info("{}:{}, ", entry.getKey(), entry.getValue());
			}
			logger.info("]\r\n");
			
			logger.info("\tSUM>=15[ ");
			for(Map.Entry<Integer, Integer> entry : mapCountOfMax15.entrySet()){
				logger.info("{}:{}, ", entry.getKey(), entry.getValue());
			}
			logger.info("]\r\n");
			
			logger.info("\tSUM>=20[ ");
			for(Map.Entry<Integer, Integer> entry : mapCountOfMax20.entrySet()){
				logger.info("{}:{}, ", entry.getKey(), entry.getValue());
			}
			logger.info("]\r\n");
			
			//统计连续o/x的个数
			ISequentialStastic seqStastic = new SequentialForSection();
			seqStastic.run(totalResult, i);
			for(int seq=1; seq<=seqStastic.getMaxCountOfSeq(); seq++){
				logger.info("\tSEQ {} {x:{}, o:{}}\r\n", seq,
						seqStastic.getCountOfSeqX().get(seq)==null?0:seqStastic.getCountOfSeqX().get(seq),
						seqStastic.getCountOfSeqO().get(seq)==null?0:seqStastic.getCountOfSeqO().get(seq));
			}
		//}
		logger.info("--------------------------------------------------\r\n");
		
	}
}