using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Xamarin.Forms;
using Captcha.ViewModel;
using Captcha;
using Captcha.Models;

namespace Captcha
{
    public partial class MainPage : ContentPage
    {
        private VMcaptcha captchaBindingContext;


        public MainPage()
        {

            InitializeComponent();
            captchaBindingContext =  new VMcaptcha(Navigation, 5, "Captcha.", "Fail");
            myCaptcha.BindingContext = captchaBindingContext;
            captchaBindingContext = myCaptcha.BindingContext as VMcaptcha;

        }
        private void Button_Clicked(object sender, EventArgs e)
        {
            tryCaptcha(captchaBindingContext);
        }
        private async Task tryCaptcha(VMcaptcha send)
        {
            if(await captchaBindingContext.TryCaptcha(send.captchasession.ImageFieldName, 
                send.captchasession.AudioFieldName,
                send.selectedImage.value, 
                send.audioTxt) == true) await Navigation.PushAsync(new Logged());
        }
    }
}
