package util.htmlparser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.lottery.LotteryHandicap;
import models.lottery.LotteryHilo;
import models.lottery.LotteryWinLoss;
import models.lottery.LotteryWinPoint;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import util.DateUtil;

import controllers.Application;

public class NbaLotteryHtmlParser {
	
	
	public static List<List<String>> getLotteryUrl(String url) throws Exception{
		List<List<String>> result = new ArrayList<List<String>>();
		Document doc = UrlUtil.getURLContent(url);
		Element basElement = doc.getElementsByTag("table").get(1);
		Elements trElements = basElement.getElementsByTag("tr");
		trElements.remove(0);
		for (Element tr : trElements){
			List<String> values = new ArrayList<String>();
			Elements tds  = tr.getElementsByTag("td");
			String tmp = tds.get(1).text();
			if  (!tmp.equals("美国职业篮球联盟")) {
				continue;
			}
			
			Element td = tds.get(2);
			
			String teams = td.getElementsByTag("a").text();
			String key = "";
			key = changeTeamName(teams.split("VS")[1].trim() +"队");
			key = changeTeamName(teams.split("VS")[0].trim() +"队") + "_" + key;
			values.add(key);
			
			String gameDate = "20" +  tds.get(3).text();
			values.add(gameDate);
			
			String statisticUrl = td.getElementsByTag("a").get(0).attr("href");
			values.add(statisticUrl);	
			result.add(values);
		}		
		return result;		
	}
	
	public static Map<String,List> getAllLotteries(Document doc) throws Exception{
		Map<String,List> result = new HashMap<String, List>();
		List<LotteryWinLoss> winlosses = new ArrayList<LotteryWinLoss>();
		List<LotteryHandicap> handicaps = new ArrayList<LotteryHandicap>();
		List<LotteryHilo> hilos = new ArrayList<LotteryHilo>();
		
		Elements tables = doc.getElementsByTag("table");
		Element table  = tables.get(0);
		Elements trs = table.getElementsByTag("tr");
		if (trs.size() > 2){
			for (int i = 2;i<trs.size();i++){
				Elements tds = trs.get(i).getElementsByTag("td");
				LotteryWinLoss wl = new LotteryWinLoss();
				wl.home_win = Double.valueOf(tds.get(1).text().trim());
				wl.guest_win = Double.valueOf(tds.get(0).text().trim());
				wl.createDate = DateUtil.parseDate(tds.get(6).text().trim() + " " + tds.get(7).text().trim(),"yyyy-MM-dd HH:mm:ss" );
				winlosses.add(wl);
				
			}
		}
		result.put("wl", winlosses);
		
		table  = tables.get(1);
		trs = table.getElementsByTag("tr");
		if (trs.size() > 2){
			for (int i = 2;i<trs.size();i++){
				Elements tds = trs.get(i).getElementsByTag("td");
				LotteryHandicap handicap = new LotteryHandicap();
				handicap.win = Double.valueOf(tds.get(2).text().trim());
				handicap.lose = Double.valueOf(tds.get(0).text().trim());
				handicap.handicap = Double.valueOf(tds.get(1).text().trim());
				String year = DateUtil.getDateStr(new Date(), "yyyy");
				String date = year + "-" + tds.get(3).text().trim() + " " + tds.get(4).text().trim();
				handicap.createDate = DateUtil.parseDate(date,"yyyy-MM-dd HH:mm:ss" );
				handicaps.add(handicap);
			}
		}
		result.put("handicap", handicaps);
		
		table  = tables.get(2);
		trs = table.getElementsByTag("tr");
		if (trs.size() > 2){
			for (int i = 2;i<trs.size();i++){
				Elements tds = trs.get(i).getElementsByTag("td");
				LotteryHilo hilo = new LotteryHilo();
				hilo.less_than = Double.valueOf(tds.get(2).text().trim());
				hilo.greater_than = Double.valueOf(tds.get(0).text().trim()); 
				hilo.hilo = Double.valueOf(tds.get(1).text().trim()); 
				String year = DateUtil.getDateStr(new Date(), "yyyy");
				String date = year + "-" + tds.get(3).text().trim() + " " + tds.get(4).text().trim();
				hilo.createDate = DateUtil.parseDate(date,"yyyy-MM-dd HH:mm:ss" );
				hilos.add(hilo);
			}
		}
		result.put("hilo", hilos);
		
		
		return result;
	}
	
	
	public static Map<String,String> getMatchResults(Document doc){
		Map resultMap = new HashMap<String,String>();
		Element tableEle = doc.getElementsByClass("tbl").get(0);
		List<Element> trs = tableEle.getElementsByTag("tr");
		for (Element trEle : trs){
			String trClass = trEle.attr("class");
			if (trClass.equals("trh"))
				continue;
			Element tdNode = trEle.getElementsByTag("td").get(2);
			if (!tdNode.text().trim().equals("美国职业篮球联盟"))
				continue;
			
			tdNode = trEle.getElementsByTag("td").get(9);
			if (tdNode.text().indexOf("进行中") >=  0){
				continue;
			}
			
			tdNode = trEle.getElementsByTag("td").get(0);
			String key = tdNode.text() + "_";
			tdNode = trEle.getElementsByTag("td").get(3);
			key = key + changeTeamName(tdNode.text().split("VS")[0].trim()+"队") 
					+ "-" + changeTeamName(tdNode.text().split("VS")[1].trim()+"队");
			String url = Application.poolResult + trEle.getElementsByTag("td").get(10).getElementsByTag("a").get(0).attr("href");
			resultMap.put(key, url);			
			
		}
		
		return resultMap;
	}
	
	public static List<LotteryWinLoss> getMacthResultWinLos(Document doc) throws ParseException{
		List<LotteryWinLoss> lotteries = new ArrayList<LotteryWinLoss>();
		Element table = doc.getElementsByTag("table").get(1);
		List<Element> trs = table.getElementsByTag("tr");
		for (Element tr : trs ){
			List<Element> tds = tr.getElementsByTag("td");
			if (tds == null || tds.size() == 0){
				continue;
			}
			
			Element td = tds.get(0);
			String tdText = td.text();			
			if ( tdText.indexOf("固定奖金") >= 0 || tdText.indexOf("彩果") >= 0 ){
				continue;
			}
			
			LotteryWinLoss wl = new LotteryWinLoss();
			wl.createDate = DateUtil.parseDate(td.text().trim(),"yyyy-MM-dd hh:mm:ss");
			wl.home_win = Double.parseDouble(tds.get(2).text());
			wl.guest_win = Double.parseDouble(tds.get(1).text());
			wl.del_marker = 1;
			lotteries.add(wl);
		}
		if (lotteries.size() > 0)
			lotteries.get(lotteries.size() - 1).del_marker = 0;
		return lotteries;
	}
	
	public static List<LotteryHandicap> getMacthResultHandcaps(Document doc) throws ParseException{
		List<LotteryHandicap> lotteries = new ArrayList<LotteryHandicap>();
		Element table = doc.getElementsByTag("table").get(2);
		List<Element> trs = table.getElementsByTag("tr");
		for (Element tr : trs ){
			List<Element> tds = tr.getElementsByTag("td");
			if (tds == null || tds.size() == 0){
				continue;
			}
			
			Element td = tds.get(0);
			String tdText = td.text();			
			if ( tdText.indexOf("固定奖金") >= 0 || tdText.indexOf("彩果") >= 0 ){
				continue;
			}
			
			if (tds.get(3).text().trim().equals("--")||td.text().trim().indexOf(":")<0){
				continue;
			}
			
			LotteryHandicap hd = new LotteryHandicap();
			hd.createDate = DateUtil.parseDate(td.text().trim(),"yyyy-MM-dd hh:mm:ss");
			hd.win = Double.parseDouble(tds.get(3).text());
			hd.lose = Double.parseDouble(tds.get(1).text());
			hd.handicap = Double.parseDouble(tds.get(2).text());
			hd.del_marker = 1;
			lotteries.add(hd);
		}	
		
		if (lotteries.size() > 0)
			lotteries.get(lotteries.size() - 1).del_marker = 0;
		return lotteries;
	}

	public static List<LotteryHilo> getMacthResultHilos(Document doc) throws ParseException{
		List<LotteryHilo> lotteries = new ArrayList<LotteryHilo>();
		Element table = doc.getElementsByTag("table").get(3);
		List<Element> trs = table.getElementsByTag("tr");
		for (Element tr : trs ){
			List<Element> tds = tr.getElementsByTag("td");
			if (tds == null || tds.size() == 0){
				continue;
			}
			
			Element td = tds.get(0);
			String tdText = td.text();			
			if ( tdText.indexOf("固定奖金") >= 0 || tdText.indexOf("彩果") >= 0 ){
				continue;
			}
			
			LotteryHilo hilo = new LotteryHilo();
			hilo.createDate = DateUtil.parseDate(td.text().trim(),"yyyy-MM-dd hh:mm:ss");
			hilo.greater_than = Double.parseDouble(tds.get(1).text());
			hilo.less_than = Double.parseDouble(tds.get(3).text());
			hilo.hilo = Double.parseDouble(tds.get(2).text());
			hilo.del_marker = 1;
			lotteries.add(hilo);
		}
		
		if (lotteries.size() > 0)
			lotteries.get(lotteries.size() - 1).del_marker = 0;
		return lotteries;
	}
	
	public static List<LotteryWinPoint> getMacthResultWinPoints(Document doc) throws ParseException{
		List<LotteryWinPoint> lotteries = new ArrayList<LotteryWinPoint>();
		Element table = doc.getElementsByTag("table").get(4);
		List<Element> trs = table.getElementsByTag("tr");
		for (Element tr : trs ){
			List<Element> tds = tr.getElementsByTag("td");
			if (tds == null || tds.size() == 0){
				continue;
			}
			
			Element td = tds.get(0);
			String tdText = td.text();			
			if ( tdText.indexOf("固定奖金") >= 0 || tdText.indexOf("彩果") >= 0 ){
				continue;
			}
			
			LotteryWinPoint wp = new LotteryWinPoint();
			wp.createDate = DateUtil.parseDate(td.text().trim(),"yyyy-MM-dd hh:mm:ss");
			wp.guest_1to5 = Double.parseDouble(tds.get(1).text());
			wp.guest_6to10 = Double.parseDouble(tds.get(2).text());
			wp.guest_11to15 = Double.parseDouble(tds.get(3).text());
			wp.guest_16to20 = Double.parseDouble(tds.get(4).text());
			wp.guest_21to25 = Double.parseDouble(tds.get(5).text());
			wp.guest_26 = Double.parseDouble(tds.get(6).text());
			
			
			wp.host_1to5 = Double.parseDouble(tds.get(7).text());
			wp.host_6to10 = Double.parseDouble(tds.get(8).text());
			wp.host_11to15 = Double.parseDouble(tds.get(9).text());
			wp.host_16to20 = Double.parseDouble(tds.get(10).text());
			wp.host_21to25 = Double.parseDouble(tds.get(11).text());
			wp.host_26 = Double.parseDouble(tds.get(12).text());
			wp.del_marker = 1;
			lotteries.add(wp);
		}
		
		if (lotteries.size() > 0)
			lotteries.get(lotteries.size() - 1).del_marker = 0;
		return lotteries;
	}
	
	
	public static void main(String[] args){
		Document doc;
		try {
			getLotteryUrl("http://info.sporttery.cn/basketball/mnl_list.php");
//			doc = UrlUtil.getURLContent("http://info.sporttery.cn/basketball/mnl_list.php");
//			getAllLotteries(doc);
//			getPlayerStatisticEspn(doc,2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}	
	
	public static String  changeTeamName(String name){
		String result = name;		
		if (name.equals("克利夫兰骑士队")){
			result = "克里夫兰骑士队";
		}else if (name.equals("休斯敦火箭队")){
			result = "休斯顿火箭队";
		}else if (name.equals("犹他爵士队")){
			result = "尤他爵士队";
		}else if (name.equals("印第安那步行者队")){
			result = "印第安纳步行者队";
		}else if (name.equals("新泽西篮网队")){
			result = "布鲁克林篮网队";
		}
		return result;
	}
}
