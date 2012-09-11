package music.web;

public class GroupInfo {

	public String group="";
	public String link="";
	public String style="";
	public String loc="";
	public String id="";
	
		
	
	public GroupInfo(String group, String link, String style, String loc, String id) {
		super();
		this.group = group;
		this.link = link;
		this.style = style;
		this.loc = loc;
		this.id = id;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public String getLoc() {
		return loc;
	}
	public void setLoc(String loc) {
		this.loc = loc;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return("Group: "+this.group+", link: "+this.link+", style: "+this.style+",location: "+this.loc+", id:"+this.id);
	}
	
	
	
}
