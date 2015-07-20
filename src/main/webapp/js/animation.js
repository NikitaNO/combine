var posts = [];
var temp;

$(document).ready(function(){
    getAllElements();
});

var getAllElements = function(){
    $('.post').each(function(i){
        var height = parseInt($(this).find(".description").css('height'));
        var lineHeight = parseInt($(this).find(".description").css('line-height'));
        var margin = parseInt($('.title').css('height')) + parseInt($('.title-block').css('padding-top')) + parseInt($('.title-block').css('padding-bottom'));
        var titleHeight = parseInt($(this).find('.title').css('height'));
        var titleLineHeight = parseInt($(this).find('.title').css('line-height'));
        var titleLines = titleHeight/titleLineHeight;
        var linesNumber = height/lineHeight;

        var post = $('.post').toArray();
        var context = $('.post > div:nth-child(1)').toArray();

        if(titleLines > 1){
            posts[i] = {};
            $(this).find('.context').css('height', '130px');
            $(this).find('.right-side').css('height', '142px');
            posts[i].originalContextHeight = '130px';
            posts[i].originalRightHeight = '142px';
        }
        else{
            posts[i] = {};
            posts[i].originalContextHeight = '120px';
            posts[i].originalRightHeight = '132px';
        }

        if(linesNumber > 3){

            $(post[i]).css('cursor', 'pointer');
            $(context[i]).append('<div class="post-arrow"></div>');
            var arrow = $(context[i]).find('.post-arrow');
            posts[i].animate = true;
            posts[i].hide = true;
            posts[i].lines = linesNumber;
            posts[i].height = height;
            posts[i].margin = margin;
            posts[i].title = titleLineHeight;
            posts[i].arrow = arrow;
        }
        else{
            posts[i].animate = false;
        }
    });

    temp = posts.length;
};

var anim = function(el){
    var id = el.getAttribute('data-id');

    if(posts[id].animate === true && posts[id].lines > 3) {
        if(posts[id].hide === true) {
            $('#' + id).animate({
                height: (posts[id].height + posts[id].margin + posts[id].title)  + "px"
            }, 300, function () {
                posts[id].hide = false;
                console.log("show");
            });

            $(posts[id].arrow).css('transform', 'rotate('+180+'deg)');
            $(posts[id].arrow).css('-webkit-transform', 'rotate('+180+'deg)');
            $(posts[id].arrow).css('-moz-transform', 'rotate('+180+'deg)');
            $(posts[id].arrow).css('-o-transform', 'rotate('+180+'deg)');

            $('.' + id).animate({
                height: (posts[id].height + posts[id].margin + posts[id].title)  + "px"
            }, 300, function () {
            });

        }

        if (posts[id].hide === false) {
            $('#' + id).animate({
                height: posts[id].originalContextHeight
            }, 300, function () {
                posts[id].hide = true;
                console.log("hide");
            });

            $(posts[id].arrow).css('transform', 'rotate('+0+'deg)');
            $(posts[id].arrow).css('-webkit-transform', 'rotate('+0+'deg)');
            $(posts[id].arrow).css('-moz-transform', 'rotate('+0+'deg)');
            $(posts[id].arrow).css('-o-transform', 'rotate('+0+'deg)');

            $('.' + id).animate({
                height: posts[id].originalRightHeight
            }, 300, function () {
            });
        }
    }
};