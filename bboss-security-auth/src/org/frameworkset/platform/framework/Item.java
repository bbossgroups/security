package org.frameworkset.platform.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.frameworkset.spi.BaseApplicationContext;
import org.frameworkset.web.servlet.support.RequestContextUtils;

 

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * Company: iSany
 * </p>
 * 
 * @author biaoping.yin
 * @version 1.0
 */
public class Item extends BaseMenuItem {
	
	private String menu;
	private String top;
	private String bottom;
	private String main;
	private String left;
	private String left_cols = "30";
	private static final Logger log = Logger.getLogger(Item.class);
	
	
	private String navigatorToolbar;
	private String navigatorContent;

	private String workspaceToolbar;
	private String workspaceContent;

	private String statusToolbar;
	private String statusContent;

	private String ancestor = null;
	
	/**
	 * 用来存放工作区链接的扩展属性
	 */
	private Map<String,String> workspacecontentExtendAttribute;


	private boolean isdefault = false;

	

	private boolean isMain = false;

	private String top_height;
	private String navigator_width;
	private String workspace_height;

	private String showhidden;
	
	
	/**用来识别区域信息，符合三一风格*/
	private String area;
	
	
	private Map<Locale,String> localLogoimages;

	public void setArea(String area) {
		this.area = area;
	}

	public Item() {

	}

	// public void addModule(Module module) {
	// modules.add(module);
	// }

	public String getBottom() {
		return bottom;
	}

	
	public String getLeft() {
		return left;
	}
	
	public boolean hasLeftVaribale(){
		return this.leftItemUrlStruction != null;
	}
	

	public String getMain() {
		return main;
	}

	public String getMenu() {
		return menu;
	}

	
	public String getTop() {
		return top;
	}
	
	public boolean hasTopVaribale(){
		return this.topItemUrlStruction != null;
	}
	



	public String getNavigatorToolbar() {
		return navigatorToolbar;
	}
	
	public boolean hasNavigatorToolbarVariables()
	{
		return this.navigatorToolbarItemUrlStruction != null;
	}
	

	public String getNavigatorContent() {
		return navigatorContent;
	}
	
	public boolean hasNavigatorContentVariables(){
		return this.navigatorContentItemUrlStruction != null;
	}
	

	public String getStatusContent() {
		return statusContent;
	}
	
	public boolean hasStatusContentVariables(){
		return this.statusContentItemUrlStruction != null;
	}

	public String getStatusToolbar() {
		return statusToolbar;
	}
	
	public boolean hasStatusToolbarVariables(){
		return this.statusToolbarItemUrlStruction != null;
	}
	

	public String getWorkspaceContent() {
		return workspaceContent;
	}
	
	public boolean hasWorkspaceContentVariables(){
		return this.workspaceContentItemUrlStruction != null;
	}
	

	public String getWorkspaceToolbar() {
		return workspaceToolbar;
	}
	
	public boolean hasWorkspaceToolbarVariables(){
		return this.workspaceToolbarItemUrlStruction != null;
	}

	

	public boolean isIsdefault() {
		return isdefault;
	}

	public String getPath() {
		return path != null ? path : (path = this.parentPath + "/" + id
				+ "$item");
	}

	
	public String getAncestor() {
		return ancestor;
	}



	public void setBottom(String bottom) {
		this.bottom = bottom;
	}

	
	public void setLeft(String left) {
		this.left = left;
	}

	public void setMain(String main) {
		this.main = main;
	}

	public void setMenu(String menu) {
		this.menu = menu;
	}

	// public void setModules(List modules) {
	// this.modules = modules;
	// }



	public void setTop(String top) {
		this.top = top;
	}

	

	public void setNavigatorToolbar(String navigatorToolbar) {
		this.navigatorToolbar = navigatorToolbar;
	}

	public void setNavigatorContent(String navigatorContent) {
		this.navigatorContent = navigatorContent;
	}

	public void setStatusContent(String statusContent) {
		this.statusContent = statusContent;
	}

	public void setStatusToolbar(String statusToolbar) {
		this.statusToolbar = statusToolbar;
	}

	public void setWorkspaceContent(String workspaceContent) {
		this.workspaceContent = workspaceContent;
	}

	public void setWorkspaceToolbar(String workspaceToolbar) {
		this.workspaceToolbar = workspaceToolbar;
	}

	public void setParentPath(String parentPath) {

		this.parentPath = parentPath;
		this.path = parentPath + "/" + this.id + "$" + "item";
	}

	public void setIsdefault(boolean isdefault) {
		this.isdefault = isdefault;
	}

	

	public void setAncestor(String ancestor) {
		this.ancestor = ancestor;
	}

	
//	public MenuItem getParent() {
//		if (parentPath.equals(Framework.getSuperMenu(Framework
//				.getSubsystemFromPath(parentPath)))) {
//			if (this.subSystem == null)
//				return Framework.getInstance().getRoot();
//			else
//				return Framework.getInstance(this.subSystem.getId()).getRoot();
//		}
//		if (this.subSystem == null)
//			return Framework.getInstance().getMenu(this.parentPath);
//		else
//			return Framework.getInstance(this.subSystem.getId()).getMenu(
//					this.parentPath);
//	}

	/**
	 * @return Returns the left_cols.
	 */
	public String getLeft_cols() {
		return left_cols;
	}

	/**
	 * @param left_cols
	 *            The left_cols to set.
	 */
	public void setLeft_cols(String left_cols) {
		this.left_cols = left_cols;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.frameworkset.platform.framework.MenuItem#isMain()
	 */
	public boolean isMain() {
		// TODO Auto-generated method stub
		return isMain;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.frameworkset.platform.framework.MenuItem#isMain()
	 */
	public void setIsMain(boolean isMain) {
		// TODO Auto-generated method stub
		this.isMain = isMain;
	}

	

	public String getNavigator_width() {
		return navigator_width;
	}

	public void setNavigator_width(String navigator_width) {
		this.navigator_width = navigator_width;
	}

	public String getTop_height() {
		return top_height;
	}

	public void setTop_height(String top_height) {
		this.top_height = top_height;
	}

	public String getWorkspace_height() {
		return workspace_height;
	}

	public void setWorkspace_height(String workspace_height) {
		this.workspace_height = workspace_height;
	}

	public String getShowhidden() {
		return showhidden;
	}

	public void setShowhidden(String showhidden) {
		this.showhidden = showhidden;
	}

	

	



	public void parserVarible(BaseApplicationContext propertiesContext) {
		String workspaceContent = this.getWorkspaceContent();
		String workspaceToolbar = this.getWorkspaceToolbar();
		String navigatorContent = this.getNavigatorContent();
		String navigatorToolbar = this.getNavigatorToolbar();
		String statusContent = this.getStatusContent();
		String statusToolbar = this.getStatusToolbar();
		String top = this.getTop();
		String left = this.getLeft();
		
		ItemUrlStruction temp = parseItemUrlStruction(workspaceContent,propertiesContext);
		if(temp != null)
		{
			if(temp.getTrueUrl() != null)
			{
				this.setWorkspaceContent(temp.getTrueUrl());
				temp = null;
			}
			else
			{
				workspaceContentItemUrlStruction = temp;
				temp = null;
			}
		}
			
		temp  = parseItemUrlStruction(workspaceToolbar,propertiesContext);
		if(temp != null)
		{
			if(temp.getTrueUrl() != null)
			{
				this.setWorkspaceToolbar(temp.getTrueUrl());
				temp = null;
			}
			else
			{
				workspaceToolbarItemUrlStruction = temp;
				temp = null;
			}
		}
		temp = parseItemUrlStruction(navigatorContent,propertiesContext);
		if(temp != null)
		{
			if(temp.getTrueUrl() != null)
			{
				this.setNavigatorContent(temp.getTrueUrl());
				temp = null;
			}
			else
			{
				navigatorContentItemUrlStruction = temp;
				temp = null;
			}
		}
		temp = parseItemUrlStruction(navigatorToolbar,propertiesContext);
		if(temp != null)
		{
			if(temp.getTrueUrl() != null)
			{
				this.setNavigatorToolbar(temp.getTrueUrl());
				temp = null;
			}
			else
			{
				navigatorToolbarItemUrlStruction = temp;
				temp = null;
			}
		}
		temp = parseItemUrlStruction(statusContent,propertiesContext);
		if(temp != null)
		{
			if(temp.getTrueUrl() != null)
			{
				this.setStatusContent(temp.getTrueUrl());
				temp = null;
			}
			else
			{
				statusContentItemUrlStruction = temp;
				temp = null;
			}
		}
		temp = parseItemUrlStruction(statusToolbar,propertiesContext);
		if(temp != null)
		{
			if(temp.getTrueUrl() != null)
			{
				this.setStatusToolbar(temp.getTrueUrl());
				temp = null;
			}
			else
			{
				statusToolbarItemUrlStruction = temp;
				temp = null;
			}
		}
		temp = parseItemUrlStruction(top,propertiesContext);
		if(temp != null)
		{
			if(temp.getTrueUrl() != null)
			{
				this.setTop(temp.getTrueUrl());
				temp = null;
			}
			else
			{
				topItemUrlStruction = temp;
				temp = null;
			}
		}
		temp = parseItemUrlStruction(left,propertiesContext);
		if(temp != null)
		{
			if(temp.getTrueUrl() != null)
			{
				this.setLeft(temp.getTrueUrl());
				temp = null;
			}
			else
			{
				leftItemUrlStruction = temp;
				temp = null;
			}
		}
	}

	public static ItemUrlStruction parseItemUrlStruction(String url,BaseApplicationContext propertiesContext) {
		if(url == null || url.trim().length() == 0)
			return null;
		int len = url.length();
		int i = 0;
		StringBuffer token = new StringBuffer();
		StringBuffer var = new StringBuffer();
		boolean varstart = false;
		int varstartposition = -1;

		List<Variable> variables = new ArrayList<Variable>();
		int varcount = 0;
		boolean hasproperty = false;
		List<String> tokens = new ArrayList<String>();
		while (i < len) {
			if (url.charAt(i) == '#') {
				if(i + 1 < len)
				{
					if( url.charAt(i + 1) == '[')
					{
				
						if (varstart) {
							token.append("#[").append(var.toString());
							var.setLength(0);
						}
		
						varstart = true;
		
						i = i + 2;
		
						varstartposition = i;
						var.setLength(0);
						continue;
					}
					
				}
				
			}

			if (varstart) {
				if (url.charAt(i) == '&') {
					varstart = false;
					i++;
					token.append("#[").append(var.toString());
					var.setLength(0);
					continue;
				} else if (url.charAt(i) == ']') {
					if (i == varstartposition) {
						varstart = false;
						i++;
						token.append("#[]");
						continue;
					} else {
						String vname = var.toString();
						if(vname.startsWith("p:"))
						{
							hasproperty = true;
							vname = vname.substring(2);
							String pvalue = propertiesContext.getProperty(vname);
							if(pvalue != null && !pvalue.equals(""))
								token.append(pvalue);
							else
								log.debug("p:"+vname + " in " + url + " don't defined correct in config file " + propertiesContext.getConfigfile());
						}
						else
						{
							Variable variable = new Variable();
							variable.setPosition(varcount);
							variable.setVariableName(var.toString());
							variables.add(variable);
							tokens.add(token.toString());
							token.setLength(0);							
							varcount++;
							
						}
						var.setLength(0);
						varstart = false;
						i++;
						
					}
				} else {
					var.append(url.charAt(i));
					i ++;
				}

			} else {
				token.append(url.charAt(i));
				i ++;
			}
		}
		if (token.length() > 0) {
			if (var.length() > 0) {
				token.append("#[").append(var.toString());
			}
			tokens.add(token.toString());
		} else {
			if (var.length() > 0) {
				token.append("#[").append(var.toString());
				tokens.add(token.toString());
			}

		}

		if (variables.size() == 0)
		{
			if(hasproperty)
			{
				ItemUrlStruction itemUrlStruction = new ItemUrlStruction();
				StringBuffer trueUrl = new StringBuffer();
				for(String temp :tokens)
				{
					trueUrl.append(temp);
				}
				itemUrlStruction.setTrueUrl(trueUrl.toString());
				return itemUrlStruction;
			}
			else
			{
				return null;
			}
		}
		else {
			ItemUrlStruction itemUrlStruction = new ItemUrlStruction();
			itemUrlStruction.setTokens(tokens);
			itemUrlStruction.setVariables(variables);			
			return itemUrlStruction;
		}

	}

	// private ItemUrlStruction parserVaribleStruction(String url)
	// {
	//
	//
	//
	// int token=0;
	// int startindex = url.indexOf("#[");
	// int andindex = url.indexOf("&");
	// int endindex = url.indexOf("]");
	// if(startindex < 0 || endindex < 0 )
	// {
	// return null;
	// }//|| (andindex > 0 && endindex > andindex)
	// else if(endindex < startindex)
	// {
	// if(startindex + 1== url.length() )
	// return null;
	// }
	// else if(endindex > startindex)
	// {
	// if(endindex + 1 == url.length() || (andindex > 0 && endindex > andindex))
	// return null;
	// }
	// else
	// {
	// ItemUrlStruction itemUrlStruction = new ItemUrlStruction();
	//
	// List<Variable> variables = new ArrayList<Variable>();
	// List<String> tokens = new ArrayList<String>();
	// tokens.add(url.substring(0,startindex));
	// Variable var = new Variable();
	// var.setPosition(0);
	// var.setVariableName(url.substring(startindex + 2,endindex));
	// variables.add(var);
	// }
	// int position = 0;
	// while(true)
	// {
	//
	// if(url.length() == endindex + 1)
	// break;
	// position ++;
	// url = url.substring(endindex + 1);
	// startindex = url.indexOf("#[");
	// andindex = url.indexOf("&");
	// endindex = url.indexOf("]");
	// if(startindex < 0 || endindex < 0 || endindex < startindex || (andindex >
	// 0 && endindex > andindex))
	// {
	// tokens.add(url);
	// break;
	// }
	// tokens.add(url.substring(0,startindex));
	// var = new Variable();
	// var.setPosition(position);
	// var.setVariableName(url.substring(startindex + 2,endindex));
	// variables.add(var);
	// }
	//
	//
	// itemUrlStruction.setVariables(variables);
	// itemUrlStruction.setTokens(tokens);
	//
	//
	// return itemUrlStruction;
	//
	// }

	private ItemUrlStruction workspaceContentItemUrlStruction;
	private ItemUrlStruction workspaceToolbarItemUrlStruction;
	private ItemUrlStruction navigatorContentItemUrlStruction;
	private ItemUrlStruction navigatorToolbarItemUrlStruction;
	private ItemUrlStruction statusContentItemUrlStruction;
	private ItemUrlStruction statusToolbarItemUrlStruction;
	private ItemUrlStruction topItemUrlStruction;
	private ItemUrlStruction leftItemUrlStruction;

	

	public static class ItemUrlStruction {
		private List<String> tokens;
		private List<Variable> variables;
		private String trueUrl;

		public List<String> getTokens() {
			return tokens;
		}

		public void setTokens(List<String> tokens) {
			this.tokens = tokens;
		}

		public List<Variable> getVariables() {
			return variables;
		}

		public void setVariables(List<Variable> variables) {
			this.variables = variables;
		}

		public String getTrueUrl() {
			return trueUrl;
		}

		public void setTrueUrl(String trueUrl) {
			this.trueUrl = trueUrl;
		}

	}

	public static class Variable {
		private String variableName;
	
		public String getVariableName() {
			return variableName;
		}

		public void setVariableName(String variableName) {
			this.variableName = variableName;
			
		}

		public int getPosition() {
			return position;
		}

		public void setPosition(int position) {
			this.position = position;
		}
		

		private int position;
		
	}

	public static void main(String[] args) {
		System.out.println("p:aaa".substring(2));
		String url = "http://localhost:80/detail.html?user=#[account]&password=#[password]love";
		
		 url =
		 "http://localhost:80/detail.html?user=#[account&password=password]&love=#[account]";
		 url =
			 "http://localhost:80/detail.html?user=#[account&password=password]&love=#[account";
		 
		 url =
			 "http://localhost:80/detail.html?user=#[account&password=#[password&love=#[account";
		 url =
			 "http://localhost:80/detail.html";
		 
		 url =
			 "http://localhost:80/#[]detail.html";
//		 url =
//			 "#[account";
		 System.out.println("url:"+url);
		// Item item = new Item();
		ItemUrlStruction a = Item.parseItemUrlStruction(url,null);
		// Map<String,String> map = new HashMap<String, String>();
		// map.put("account", "aaa");
		// map.put("password", "123");
		// item.combinationItemUrlStruction(a, map);

		if(a != null){
			
		
		List<String> tokens = a.getTokens();
		for (int k = 0; k < tokens.size(); k++) {
			System.out.println("tokens[" + k + "]:" + tokens.get(k));
		}
		List<Variable> variables = a.getVariables();

		for (int k = 0; k < variables.size(); k++) {

			Variable as = variables.get(k);

			System.out.println("变量名称：" + as.getVariableName());
			System.out.println("变量对应位置：" + as.getPosition());

		}
		}
	}

	public ItemUrlStruction getWorkspaceContentItemUrlStruction() {
		return workspaceContentItemUrlStruction;
	}

//	public void setWorkspaceContentItemUrlStruction(
//			ItemUrlStruction workspaceContentItemUrlStruction) {
//		this.workspaceContentItemUrlStruction = workspaceContentItemUrlStruction;
//	}

	public ItemUrlStruction getWorkspaceToolbarItemUrlStruction() {
		return workspaceToolbarItemUrlStruction;
	}

//	public void setWorkspaceToolbarItemUrlStruction(
//			ItemUrlStruction workspaceToolbarItemUrlStruction) {
//		this.workspaceToolbarItemUrlStruction = workspaceToolbarItemUrlStruction;
//	}

	public ItemUrlStruction getNavigatorContentItemUrlStruction() {
		return navigatorContentItemUrlStruction;
	}

//	public void setNavigatorContentItemUrlStruction(
//			ItemUrlStruction navigatorContentItemUrlStruction) {
//		this.navigatorContentItemUrlStruction = navigatorContentItemUrlStruction;
//	}

	public ItemUrlStruction getNavigatorToolbarItemUrlStruction() {
		return navigatorToolbarItemUrlStruction;
	}

//	public void setNavigatorToolbarItemUrlStruction(
//			ItemUrlStruction navigatorToolbarItemUrlStruction) {
//		this.navigatorToolbarItemUrlStruction = navigatorToolbarItemUrlStruction;
//	}

	public ItemUrlStruction getStatusContentItemUrlStruction() {
		return statusContentItemUrlStruction;
	}

//	public void setStatusContentItemUrlStruction(
//			ItemUrlStruction statusContentItemUrlStruction) {
//		this.statusContentItemUrlStruction = statusContentItemUrlStruction;
//	}

	public ItemUrlStruction getStatusToolbarItemUrlStruction() {
		return statusToolbarItemUrlStruction;
	}

//	public void setStatusToolbarItemUrlStruction(
//			ItemUrlStruction statusToolbarItemUrlStruction) {
//		this.statusToolbarItemUrlStruction = statusToolbarItemUrlStruction;
//	}

	public ItemUrlStruction getTopItemUrlStruction() {
		return topItemUrlStruction;
	}

//	public void setTopItemUrlStruction(ItemUrlStruction topItemUrlStruction) {
//		this.topItemUrlStruction = topItemUrlStruction;
//	}

	public ItemUrlStruction getLeftItemUrlStruction() {
		return leftItemUrlStruction;
	}

	
	
	  public String getArea()
	  {
		  return this.area;
	  }

	public Map<String, String> getWorkspacecontentExtendAttribute() {
		return workspacecontentExtendAttribute;
	}
	
	public String getWorkspacecontentExtendAttribute(String attribute) {
		return workspacecontentExtendAttribute != null ?workspacecontentExtendAttribute.get(attribute):null;
	}
	
	public String getLogoImage(HttpServletRequest request) {
		if(this.localLogoimages == null)
			return workspacecontentExtendAttribute != null ?workspacecontentExtendAttribute.get("logoimage"):null;
		else
		{
			Locale locale = RequestContextUtils.getRequestContextLocal(request);
	    	String temp = this.localLogoimages.get(locale);
	    	if(temp == null)
	    		return workspacecontentExtendAttribute != null ?workspacecontentExtendAttribute.get("logoimage"):null;
	    	return temp;
		}
				
	}

	public void setWorkspacecontentExtendAttribute(
			Map<String, String> workspacecontentExtendAttribute) {
		this.workspacecontentExtendAttribute = workspacecontentExtendAttribute;
	}

	

	public Map<Locale, String> getLocalLogoimages() {
		return localLogoimages;
	}

	public void setLocalLogoimages(Map<Locale, String> localLogoimages) {
		this.localLogoimages = localLogoimages;
	}



//	public void setLeftItemUrlStruction(ItemUrlStruction leftItemUrlStruction) {
//		this.leftItemUrlStruction = leftItemUrlStruction;
//	}
}
