function handleStarResult(resultData) {
    console.log("handleStarResult: populating star table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");
    console.log("here");
    console.log(resultData.length);
    // Iterate through resultData, no more than 10 entriess
    for (let i = 0; i < Math.min(20, resultData.length); i++) {

        // Concatenate the html tags with resultData jsonObject
        console.log("ite");
        let rowHTML = "";
        rowHTML += "<tr>";
        /*
        rowHTML +=
            "<th>" +
            // Add a link to single-star.html with id passed with GET url parameter
            '<a href="single-star.html?id=' + resultData[i]['star_id'] + '">'
            + resultData[i]["star_name"] +     // display star_name for the link text
            '</a>' +
            "</th>";
        rowHTML += "<th>" + resultData[i]["star_dob"] + "</th>";
        */
        rowHTML += "<th>" + '<a href="single-movie.html?title=' + resultData[i]["movie_title"] + '">'
            + resultData[i]["movie_title"] +     // display star_name for the link text
            '</a>' + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        //rowHTML += "<th>" + resultData[i]["movie_genres"] + "</th>";
        const myArray1 = resultData[i]["movie_genres"].split(",");
        rowHTML += "<th>";
        for (let i = 0; i < myArray1.length; i++)
        {
            rowHTML += '<a href="BrowseMovie.html?sorted=tara&load_size=25&offset=0&genre=' + myArray1[i] + '">'
                + myArray1[i] +
                '</a>' + "||";
        }
        rowHTML += "</th>";
        const myArray = resultData[i]["movie_stars"].split(",");
        rowHTML += "<th>";
        for (let i = 0; i < myArray.length; i++)
        {
            rowHTML += '<a href="single-star.html?name=' + myArray[i] + '">'
                + myArray[i] +     // display star_name for the link text
                '</a>' + "||";
        }
        rowHTML += "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
        console.log("done");
    }
}

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
    genresAppend += '<a href="BrowseMovie.html?sorted=tara&load_size=25&offset=0&genre=' + genre_set[i] + '">' + genre_set[i] + '</a>'+ "   ";
}
genresList.append(genresAppend);
let charAppend = "";
for (let i = 0; i < char_set.length; ++i)
{
    charAppend += '<a href="BrowseMovie.html?sorted=tara&load_size=25&offset=0&char=' + char_set[i] + '">' + char_set[i] + '</a>'+ "   ";
}
charList.append(charAppend);
/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */
let current = window.location.href;
let index = current.indexOf("BrowseMovie");
let to = current.slice(index,current.length);
to = "api/" + to;
console.log(to);
// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: to, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});