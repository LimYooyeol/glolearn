$('#sortSelect').change(function(){
    var base = document.getElementById('pagingBase').value
    var category = ''
    if(document.getElementById('pageCategory') == null){
        category = base + '/' + 'all'
    }else{
        category = base + '/' + document.getElementById('pageCategory').value.toLowerCase()
    }

    if(document.getElementById('pageSearch') == null){
        window.location.href = category + '?sort=' + this.value.toLowerCase()
    }else{
        window.location.href = category + '?sort=' + this.value.toLowerCase() + '&' + 'search=' + document.getElementById('pageSearch').value
    }
})

function changeCategory(category){
    var href = document.getElementById('pagingBase').value + '/' + category
    window.location.href = href
}

function search(){
    var key = document.getElementById('searchKey').value

    base = document.getElementById('pagingBase').value

    var category = ''
    if(document.getElementById('pageCategory') == null){
        category = base + '/' + 'all'
    }else{
        category = base + '/' + document.getElementById('pageCategory').value
    }

    var sort = document.getElementById('pageSort').value.toLowerCase()
    if(key == ''){
        window.location.href = category + '?sort=' + sort
    }else{
        window.location.href = category + '?sort=' + sort + '&' + 'search=' + key
    }
}

function movePage(pageNum){
    base = document.getElementById('pagingBase').value

    var category = ''
    if(document.getElementById('pageCategory')== null){
        category = base + '/all'
    }else{
        category = base + '/' + document.getElementById('pageCategory').value
    }

    var sort = document.getElementById('pageSort').value

    var page = 1
    if(pageNum == -1){
        page = parseInt(document.getElementById('pageNum').value) - 1;
    }
    else if(pageNum == -2){
        page = parseInt(document.getElementById('pageNum').value) + 1;
    }else{
        page = pageNum
    }
    if(page < 1 || page > parseInt(document.getElementById('maxPage').value)){
        alert('마지막 페이지입니다.')
        return
    }

    if(document.getElementById('pageSearch') == null){
        window.location.href = category + '?sort=' + sort + '&' + 'page=' + page
    }else{
        window.location.href = category + '?sort=' + sort + '&' + 'page=' + page + '&' + 'search=' + document.getElementById('pageSearch').value
    }
}