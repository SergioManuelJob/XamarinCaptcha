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
    class VMcaptcha : BaseViewModel
    {
        #region VARIABLES

        HttpClient client;
        private ObservableCollection<Mimage> _images = new ObservableCollection<Mimage>();
        private Mcaptchasession _captchasession = new Mcaptchasession();
        private Mimage _selectedImage = new Mimage();
        private int numIntentos { get; set; }
        private int intentosActuales { get; set; }
        private string _paginaIncorrecta { get; set; }
        private string namespaceActual { get; set; }
        private string _audioTxt;

        ISimpleAudioPlayer audioPlayer;
        private Boolean tapped { get; set; }
        private Boolean _isImagesVisible;
        private Boolean _isAudioVisible;

        #endregion
        #region CONSTRUCTOR
        public VMcaptcha(INavigation navigation, int intentos, string Namespace, string paginaIncorrecta)
        {
            Navigation = navigation;
            client = new HttpClient();
            numIntentos = intentos;
            _paginaIncorrecta = paginaIncorrecta;
            namespaceActual = Namespace;
            _isImagesVisible = true;
            _isAudioVisible = false;
            start();
        }
        #endregion
        #region OBJETOS
        public string audioTxt
        {
            get { return _audioTxt; }
            set { SetValue(ref _audioTxt, value); }
        }
        public Mcaptchasession captchasession
        {
            get { return _captchasession; }
            set
            {
                SetValue(ref _captchasession, value);
            }
        }
        public Mimage selectedImage
        {
            get { return _selectedImage; }
            set
            {
                SetValue(ref _selectedImage, value);
            }
        }
        public bool isAudioVisible
        {
            get { return _isAudioVisible; }
            set
            {
                if (_isAudioVisible != value)
                {
                    _isAudioVisible = value;
                    OnPropertyChanged(nameof(isAudioVisible));
                }
            }
        }
        public bool isImagesVisible
        {
            get { return _isImagesVisible; }
            set
            {
                if (_isImagesVisible != value)
                {
                    _isImagesVisible = value;
                    OnPropertyChanged(nameof(isImagesVisible));
                }
            }
        }
        public ObservableCollection<Mimage> Images
        {
            get { return _images; }
            set
            {
                SetValue(ref _images, value);
            }
        }

        #endregion
        #region PROCESOS
        public async Task start()
        {
            try
            {
                Uri uri = new Uri("http://192.168.1.40:8080/start/5");
                HttpRequestMessage request = new HttpRequestMessage(HttpMethod.Get, uri);
                HttpResponseMessage response = await client.SendAsync(request);
                if (response.IsSuccessStatusCode)
                {
                    captchasession = JsonConvert.DeserializeObject<Mcaptchasession>(await response.Content.ReadAsStringAsync());
                    for (int i = 0; i < 5; i++)
                    {
                        Mimage image = new Mimage();
                        MemoryStream stream = new MemoryStream(await getImage(i));
                        image.Source = ImageSource.FromStream(() => stream);
                        image.index = i;
                        image.value = captchasession.Values[i];
                        Images.Add(image);
                    }
                    byte[] audioBytes = await getAudio();
                    audioPlayer = CrossSimpleAudioPlayer.CreateSimpleAudioPlayer();
                    audioPlayer.Load(new MemoryStream(audioBytes));
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
            }
        }

        public async Task<Byte[]> getImage(int index)
        {
            try
            {
                Uri uri = new Uri("http://192.168.1.40:8080/image/" + index.ToString());
                HttpRequestMessage request = new HttpRequestMessage(HttpMethod.Get, uri);
                HttpResponseMessage response = await client.SendAsync(request);
                if (response.IsSuccessStatusCode)
                {
                    return await response.Content.ReadAsByteArrayAsync();
                }
                else { return null; }
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                return null;
            }
        }

        public async Task<Byte[]> getAudio()
        {
            try
            {
                Uri uri = new Uri("http://192.168.1.40:8080/audio");
                HttpRequestMessage request = new HttpRequestMessage(HttpMethod.Get, uri);
                HttpResponseMessage response = await client.SendAsync(request);
                if (response.IsSuccessStatusCode)
                {
                    return await response.Content.ReadAsByteArrayAsync();
                }
                else { return null; }
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                return null;
            }
        }

        public async Task<Boolean> TryCaptcha(string nameValueImages, string nameValueAudio, string valueSentImages, string valueSentAudio) {
            var sendParameter = "";
            var sendName = "";
            Console.WriteLine("argumentos pasados uwu: " + nameValueAudio + " " + valueSentAudio);
            if (valueSentImages == null)
            {
                sendName = nameValueAudio;
                sendParameter = valueSentAudio;
            }
            else
            {
                sendName = nameValueImages;
                sendParameter = valueSentImages;
            }
            Console.WriteLine("PARAMETROS: " + sendName + " " + sendParameter);
            var request = new HttpRequestMessage(HttpMethod.Post, "http://192.168.1.40:8080/try");
            var parameter = new
            {
                name = sendName,
                value = sendParameter
            };
            string parameterJson = JsonConvert.SerializeObject(parameter);
            request.Headers.Add("request", parameterJson);
            HttpResponseMessage response = await client.SendAsync(request);
            Mvalidationresponse validResponse = JsonConvert.DeserializeObject<Mvalidationresponse>(await response.Content.ReadAsStringAsync());
            if (validResponse.Valid == true) {
                return true;
            }
            else{
                intentosActuales++;
                Console.WriteLine(intentosActuales.ToString());
                if(intentosActuales == numIntentos) {
                    Type pageType = Type.GetType(namespaceActual + _paginaIncorrecta);
                    Page page = (Page)Activator.CreateInstance(pageType);
                    await Navigation.PushAsync(page);
                    return false;
                }
                else {
                    selectedImage = new Mimage();
                    Images = new ObservableCollection<Mimage>();
                    captchasession = new Mcaptchasession();
                    await start();
                    return false;
                }
                
            }
        }

        public void TapImage(string value)
        {
            selectedImage.value = value;
        }

        public async Task GetAnother()
        {
            if(tapped == false) {
                tapped = true;
                selectedImage = new Mimage();
                Images = new ObservableCollection<Mimage>();
                captchasession = new Mcaptchasession();
                await start();
                tapped = false;
            }
        }

        public void PlayAudioAndLeave()
        {
            audioPlayer.Play();
            isImagesVisible = !isImagesVisible;
            isAudioVisible = !isAudioVisible;
        }

        public async Task changeToImages()
        {
            isImagesVisible = !isImagesVisible;
            isAudioVisible = !isAudioVisible;
            if (tapped == false)
            {
                tapped = true;
                selectedImage = new Mimage();
                Images = new ObservableCollection<Mimage>();
                captchasession = new Mcaptchasession();
                await start();
                tapped = false;
            }
        }

        #endregion
        #region COMANDOS
        public ICommand tapImagecommand => new Command<Mimage>((image) => TapImage(image.value));
        public ICommand getAnothercommand => new Command(async () => await GetAnother());
        public ICommand playAudioAndLeavecommand => new Command(() => PlayAudioAndLeave());
        public ICommand changeToImagescommand => new Command(async () => await changeToImages());
        #endregion
    }
}
