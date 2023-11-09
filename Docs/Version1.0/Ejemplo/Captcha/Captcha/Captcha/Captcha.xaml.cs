using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;
using Captcha.ViewModel;
using System.IO;
using System.Net.Http;

namespace Captcha
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class Captcha : Grid
    {
        public Captcha()
        {
            InitializeComponent();
        }

    }
}