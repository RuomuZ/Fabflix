function insert_star(form){
    let l = form.star.value;
    let s = form.birthday.value;
    console.log("submit insert star form");
    console.log(s);
    console.log("api/insertStar?star=" + l + "&birthday="+s);
    $.ajax("api/insertStar?star=" + l + "&birthday="+s, {
        method: "GET",
        success: handleInsertStarResult
    });
}
function handleInsertStarResult(rs)
{
    let resultDataJson = JSON.parse(rs);

    console.log("handle insert star response");
    console.log(resultDataJson);
    console.log(resultDataJson["message"]);
    $("#insert_star_message").text(resultDataJson["message"]);

}

function add_movie(form){
    let t = form.title.value;
    let y = form.year.value;
    let d = form.director.value;
    let l = form.star.value;
    let g = form.genre.value;
    console.log("submit add movie form");
    $.ajax("api/addMovie?star=" + l + "&title="+t+ "&year="+y+ "&director="+d
        + "&genre="+g, {
        method: "GET",
        success: handleAddMovieResult

    });
}
function handleAddMovieResult(rs)
{
    let resultDataJson = JSON.parse(rs);

    console.log("handle add movie response");
    console.log(resultDataJson);
    console.log(resultDataJson["message"]);
    $("#add_movie_message").text(resultDataJson["message"]);

}

function handleMeta(rs){
    console.log("good");
    let metaElement = jQuery("#meta");
    let a = "";
    for (let i = 0; i < rs.length; ++i){
        a += "<h2>" + rs[i]["table"] + "</h2>";
        a += "<table class=\"table table-striped\">" +
            "    <tr>" +
            "        <th>field</th>" +
            "        <th>type</th>" +
            "    </tr>";
        let fields = JSON.parse(rs[i]["fields"]);
       // console.log(fields);
       // console.log(Object.keys(fields).length);
        for (let k = 0; k < Object.keys(fields).length; ++k){
            a += "<tr>";
            console.log(fields[k]);
            a += "<th>" + fields[k].field + "</th>";
            a += "<th>" + fields[k].type + "</th>";
            a += "</tr>";
        }
        a +=  "</table>";
    }
    metaElement.append(a);

}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/meta", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleMeta(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});
