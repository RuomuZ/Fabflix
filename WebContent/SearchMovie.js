var genre_set = ["Action", "Adult","Adventure","Biography","Comedy","Crime","Documentary"
,"Drama","Family","Fantasy","History","Horror","Music","Musical","Mystery","Reality-TV","Romance","Sci-Fi","Sport","Thriller"
,"War","Western"];

var char_set = ["A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W"
,"X","Y","Z","0","1","2","3","4","5","6","7","8","9","*"];

let genresList = jQuery("#genreList");
let charList = jQuery("#charList");
let genresAppend = "";
for (let i = 0; i < genre_set.length; ++i)
{
    genresAppend += '<a href="BrowseMovie.html?sorted=tara&load_size=10&offset=0&genre=' + genre_set[i] + '">' + genre_set[i] + '</a>'+ "   ";
}
genresList.append(genresAppend);
let charAppend = "";
for (let i = 0; i < char_set.length; ++i)
{
    charAppend += '<a href="BrowseMovie.html?sorted=tara&load_size=10&offset=0&char=' + char_set[i] + '">' + char_set[i] + '</a>'+ "   ";
}
charList.append(charAppend);