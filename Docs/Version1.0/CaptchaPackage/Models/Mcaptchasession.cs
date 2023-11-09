using System;
using System.Collections.Generic;
using System.Text;

namespace Captcha.Models
{
    class Mcaptchasession
    {
        public string ImageName { get; set; }
        public string ImageFieldName { get; set; }
        public List<string> Values { get; set; }
        public string AudioFieldName { get; set; }
    }
}
