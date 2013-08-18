package controllers.autojob;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import controllers.dataimport.EspnImport;

import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import util.DateUtil;

@Every("2h")
public class JobPreview  extends Job{
	
	public void doJob() {		
		Logger.info("auto preview start !!!!");
		try {
			EspnImport.importPreviews(DateUtil.getDateStr(new Date(), "yyyy-MM-dd"));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Logger.info("auto preview end !!!!");
	}

}
