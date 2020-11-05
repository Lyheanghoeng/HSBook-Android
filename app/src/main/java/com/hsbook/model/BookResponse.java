package com.hsbook.model;


import com.google.gson.annotations.SerializedName;

public class BookResponse {

	@SerializedName("book_type")
	private String bookType;

	@SerializedName("type_kh")
	private String typeKh;

	@SerializedName("author")
	private String author;

	@SerializedName("isbn")
	private String isbn;

	@SerializedName("release_year")
	private String releaseYear;

	@SerializedName("type_en")
	private String typeEn;

	@SerializedName("soft_file")
	private String softFile;

	@SerializedName("show_pub")
	private String showPub;

	@SerializedName("book_id")
	private String bookId;

	@SerializedName("book_language")
	private String bookLanguage;

	@SerializedName("entry_date")
	private String entryDate;

	@SerializedName("cover")
	private String cover;

	@SerializedName("entry_by")
	private String entryBy;

	@SerializedName("Search")
	private String search;

	@SerializedName("lang")
	private String lang;

	public void setBookType(String bookType){
		this.bookType = bookType;
	}

	public String getBookType(){
		return bookType;
	}

	public void setTypeKh(String typeKh){
		this.typeKh = typeKh;
	}

	public String getTypeKh(){
		return typeKh;
	}

	public void setAuthor(String author){
		this.author = author;
	}

	public String getAuthor(){
		return author;
	}

	public void setIsbn(String isbn){
		this.isbn = isbn;
	}

	public String getIsbn(){
		return isbn;
	}

	public void setReleaseYear(String releaseYear){
		this.releaseYear = releaseYear;
	}

	public String getReleaseYear(){
		return releaseYear;
	}

	public void setTypeEn(String typeEn){
		this.typeEn = typeEn;
	}

	public String getTypeEn(){
		return typeEn;
	}

	public void setSoftFile(String softFile){
		this.softFile = softFile;
	}

	public String getSoftFile(){
		return softFile;
	}

	public void setShowPub(String showPub){
		this.showPub = showPub;
	}

	public String getShowPub(){
		return showPub;
	}

	public void setBookId(String bookId){
		this.bookId = bookId;
	}

	public String getBookId(){
		return bookId;
	}

	public void setBookLanguage(String bookLanguage){
		this.bookLanguage = bookLanguage;
	}

	public String getBookLanguage(){
		return bookLanguage;
	}

	public void setEntryDate(String entryDate){
		this.entryDate = entryDate;
	}

	public String getEntryDate(){
		return entryDate;
	}

	public void setCover(String cover){
		this.cover = cover;
	}

	public String getCover(){
		return cover;
	}

	public void setEntryBy(String entryBy){
		this.entryBy = entryBy;
	}

	public String getEntryBy(){
		return entryBy;
	}

	public void setSearch(String search){
		this.search = search;
	}

	public String getSearch(){
		return search;
	}

	public void setLang(String lang){
		this.lang = lang;
	}

	public String getLang(){
		return lang;
	}

	@Override
 	public String toString(){
		return 
			"Response{" + 
			"book_type = '" + bookType + '\'' + 
			",type_kh = '" + typeKh + '\'' + 
			",author = '" + author + '\'' + 
			",isbn = '" + isbn + '\'' + 
			",release_year = '" + releaseYear + '\'' + 
			",type_en = '" + typeEn + '\'' + 
			",soft_file = '" + softFile + '\'' + 
			",show_pub = '" + showPub + '\'' + 
			",book_id = '" + bookId + '\'' + 
			",book_language = '" + bookLanguage + '\'' + 
			",entry_date = '" + entryDate + '\'' + 
			",cover = '" + cover + '\'' + 
			",entry_by = '" + entryBy + '\'' + 
			",search = '" + search + '\'' + 
			",lang = '" + lang + '\'' + 
			"}";
		}
}