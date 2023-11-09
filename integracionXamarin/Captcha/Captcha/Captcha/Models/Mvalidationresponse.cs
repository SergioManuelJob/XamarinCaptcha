using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Text;

namespace Captcha.Models
{
    internal class Mvalidationresponse
    {
            [JsonProperty("valid")]
            public bool Valid { get; set; }

            [JsonProperty("response")]
            public string Response { get; set; }
    }
}
