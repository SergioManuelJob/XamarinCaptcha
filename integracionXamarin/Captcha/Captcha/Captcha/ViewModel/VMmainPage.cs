using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Input;
using Xamarin.Forms;
using Captcha.Models;
using System.IO;
using System.Net.Http;
using System.Collections.ObjectModel;
using System.Xml.Linq;
using Newtonsoft.Json;
using Captcha;
using Xamarin.Essentials;
using Plugin.SimpleAudioPlayer;

namespace Captcha.ViewModel
{
    class VMmainPage : BaseViewModel
    {
        #region VARIABLES

        private VMcaptcha captchaBindingContext;
        private string _nameTxt;
        private string _passwordTxt;

        #endregion
        #region CONSTRUCTOR
        public VMmainPage(INavigation navigation, Captcha captcha)
        {
            Navigation = navigation;
            captchaBindingContext = new VMcaptcha(navigation, 5, "Captcha.", "Fail");
            captcha.BindingContext = captchaBindingContext;
        }
        #endregion
        #region OBJETOS

        public string nameTxt
        {
            get { return _nameTxt; }
            set { SetValue(ref _nameTxt, value); }
        }
        public string passwordTxt
        {
            get { return _passwordTxt; }
            set { SetValue(ref _passwordTxt, value); }
        }
        #endregion
        #region PROCESOS

        private async Task tryCaptcha(VMcaptcha context)
        {
            if (await captchaBindingContext.TryCaptcha(context.captchasession.ImageFieldName,
                context.captchasession.AudioFieldName,
                context.selectedImage.value,
                context.audioTxt) == true) await Navigation.PushAsync(new Logged());
        }
        public async Task Register()
        {
            if(string.IsNullOrEmpty(nameTxt) || string.IsNullOrEmpty(passwordTxt))
            {
                captchaBindingContext.setVisible();
            }
            else await tryCaptcha(captchaBindingContext);
        }

        #endregion
        #region COMANDOS
        public ICommand registercommand => new Command(async () => await Register());
        #endregion
    }
}
