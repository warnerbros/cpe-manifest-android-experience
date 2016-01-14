package com.wb.nextgen.util.utils;

public class FlixsterVideoDeepLinkData {
	
	public enum DeepLinkAction{
		ACTION_MOVIES("movies"), ACTION_REDEEM("redeem"), ACTION_ENV_CHANGE("env"), ACTION_WATCH("watch"), ACTION_DOWNLOAD("download");
		
		public final String value;
		
		DeepLinkAction(String value){
			this.value = value;
		}
		
		public static DeepLinkAction fromValue(String value){
			for (DeepLinkAction action : values()){
				if (action.value.equals(value))
					return action;
			}
			return null;
		}
		@Override
		public String toString(){
			return value;
		}
	}
	
	public final DeepLinkAction action;
	public final DeepLinkAction subAction;
	private boolean bConsumed = false;
	public final String guid;
	public final String contentId;
	
	public FlixsterVideoDeepLinkData(String[] actions, String[] values ){

		action = (actions.length > 0)? DeepLinkAction.fromValue(actions[0]): null;
		subAction = (actions.length> 1)?DeepLinkAction.fromValue(actions[1]): null;
		contentId = values.length > 0? values[0]: null;
		guid = values.length > 0? values[0]: null;
	}
	
	public boolean isConsumed(){
		return bConsumed;
	}
	
	public void consumeAction(){
		bConsumed = true;
	}
	
	public boolean shouldReloadStartupPage(){
		return DeepLinkAction.ACTION_MOVIES.equals(action) && bConsumed;
			
	}
	
	public boolean isWatchSubAction(){
		return DeepLinkAction.ACTION_WATCH.equals(subAction);
	}
	
	public boolean isDownloadSubAction(){
		return DeepLinkAction.ACTION_DOWNLOAD.equals(subAction);
	}
}
