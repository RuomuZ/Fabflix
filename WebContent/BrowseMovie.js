let current = window.location.href;
let index = current.indexOf("BrowseMovie");
let t = current.slice(index,current.length);
to = "api/" + t;
let load = parseInt(getParameterByName("load_size"))
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function update(form){
    let l = form.load_size.value;
    let s = form.sorted.value;
    let cl = getParameterByName("load_size")
    let cs = getParameterByName("sorted")
    let new_url = current.replace("sorted="+cs,"sorted="+s);
    new_url = new_url.replace("load_size="+cl, "load_size="+l);
    window.location.replace(new_url);
}

function handleAdd(movie_title) {
    console.log("submit cart form");

    $.ajax("api/addItem?item=" + movie_title, {
        method: "GET",
        success: resultDataString => {
            let resultDataJson = JSON.parse(resultDataString);
            handleCartArray(resultDataJson["previousItems"]);
        }
    });
    // clear input form

}
function handleCartArray(rs)
{
}



function handleStarResult(resultData) {
    console.log("handleStarResult: populating star table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");
    console.log(resultData.length);
    console.log(load);
    // Iterate through resultData, no more than 10 entriess
    for (let i = 1; i < Math.min(load + 1, resultData.length); i++) {

        // Concatenate the html tags with resultData jsonObject
        console.log(i)
        let rowHTML = "";
        rowHTML += "<tr>";
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
            rowHTML += '<a href="BrowseMovie.html?sorted=tara&load_size=10&offset=0&genre=' + myArray1[i] + '">'
                + myArray1[i] +
                '</a>' + "||";
        }
        rowHTML += "</th>";
        if (resultData[i]["movie_stars"] != null) {
            const myArray = resultData[i]["movie_stars"].split(",");
            rowHTML += "<th>";
            for (let i = 0; i < myArray.length; i++) {
                rowHTML += '<a href="single-star.html?name=' + myArray[i] + '">'
                    + myArray[i] +     // display star_name for the link text
                    '</a>' + "||";
            }
            rowHTML += "</th>";
            rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
            let movie_title = resultData[i]["movie_title"];
            rowHTML += "<th>";
        } else {rowHTML += "<th>Empty</th>>";}
        console.log(movie_title);
        rowHTML += "<button onClick='handleAdd(\"" + movie_title + "\")'>ADD</button>";


        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);


    }
    let PageElement = jQuery("#page");
    let num_rec = parseInt(resultData[0]["total"]);
    let num_page = Math.ceil(num_rec / load)
    let htap = ""
    let offset = parseInt(getParameterByName("offset"))
    let current_page = (offset / load) + 1
    if (current_page == 1)
    {
        htap += "<a href=\"#\">&laquo;</a>";
    }
    else{
        let o = offset - load
        let p = t.replace("offset=" + offset.toString(), "offset=" + o.toString())
        htap += "<a href=" + p + ">&laquo;</a>";
    }

    for (let i = 1; i <= num_page; i++)
    {
        let o = offset + (i - current_page) * load
        let p = t.replace("offset=" + offset.toString(), "offset=" + o.toString())

        if (i == current_page) {
            htap += '<a class = "active" href=' + p + '>'
                +i+'</a>';
        }
        else{
            htap += '<a href=' + p + '>'
                +i+'</a>';
        }
    }
    if (current_page == num_page)
    {
        htap += "<a href=\"#\">&raquo;</a>";
    }
    else{
        let o = offset + load
        let p = t.replace("offset=" + offset.toString(), "offset=" + o.toString())
        htap += "<a href=" + p + ">&raquo;</a>";
    }
    PageElement.append(htap)
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
    genresAppend += '<a href="BrowseMovie.html?sorted=tdrd&load_size=10&offset=0&genre=' + genre_set[i] + '">' + genre_set[i] + '</a>'+ "   ";
}
genresList.append(genresAppend);
let charAppend = "";
for (let i = 0; i < char_set.length; ++i)
{
    charAppend += '<a href="BrowseMovie.html?sorted=tdrd&load_size=10&offset=0&char=' + char_set[i] + '">' + char_set[i] + '</a>'+ "   ";
}
charList.append(charAppend);
/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

console.log(to);
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: to, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});

