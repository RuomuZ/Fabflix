function handleGenre(rs){
    let genresList = jQuery("#genreList");

    let genresAppend = "";
    for (let i = 0; i < rs.length; ++i)
    {
        genresAppend += '<a href="BrowseMovie.html?sorted=tdrd&load_size=10&offset=0&genre=' + rs[i]["name"] + '">' + rs[i]["name"] + '</a>'+ "   ";
    }
    genresList.append(genresAppend);

}

var char_set = ["A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W"
,"X","Y","Z","0","1","2","3","4","5","6","7","8","9","*"];

let charList = jQuery("#charList");

let charAppend = "";
for (let i = 0; i < char_set.length; ++i)
{
    charAppend += '<a href="BrowseMovie.html?sorted=tdrd&load_size=10&offset=0&char=' + char_set[i] + '">' + char_set[i] + '</a>'+ "   ";
}
charList.append(charAppend);

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/getGenre", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleGenre(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});