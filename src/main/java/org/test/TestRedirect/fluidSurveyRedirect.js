
<script type="text/javascript">
var getParams = window.location.search.substr(1);
var redirectLocation = 'http://127.0.0.1:8080/#' + getParams;
var delay = 100;
$(window).load(function(){
    setTimeout(function(){
    window.location = redirectLocation;
    }, delay);
});
</script>