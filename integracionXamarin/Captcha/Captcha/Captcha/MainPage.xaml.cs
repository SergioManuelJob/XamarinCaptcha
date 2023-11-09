﻿using System;
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

        public MainPage()
        {

            InitializeComponent();
            BindingContext = new VMmainPage(Navigation, myCaptcha);
        }
    }
}
