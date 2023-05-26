
// для формы поиска товаров
document.addEventListener("DOMContentLoaded", hiddenCloseclick());
document.getElementById('click-to-hide').addEventListener("click", hiddenCloseclick);
function hiddenCloseclick() {
    let x = document.getElementById('hidden-element');
    if (x.style.display == "none"){
        x.style.display = "block";
        localStorage.setItem('div', 'block');
    } else {
        x.style.display = "none";
    }
}
window.addEventListener('DOMContentLoaded', function (){
    let divHidden = document.querySelector('.hidden-element');
    if(localStorage.getItem('div') === 'block'){
        divHidden.style.display = "block";
    }
    localStorage.clear();
})
