#include <TFile.h>
#include <TMath.h>
//#include <TNtuple.h>
#include <TH1.h>
#include <TH2.h>
#include <TF1.h>
//#include <TProfile.h>
#include <TCanvas.h>
#include <TLegend.h>
//#include <TFrame.h>
//#include <TROOT.h>
//#include <TSystem.h>
//#include <TRandom3.h>
#include <TStyle.h>
#include "TRint.h"
#include "TROOT.h"
#include "TKey.h"
#include "TDirectory.h"
#include <TBenchmark.h>
#include <TInterpreter.h>
#include <iostream>
#include <string>
using namespace std;

Double_t langaufun(Double_t *x, Double_t *par) {
   //Fit parameters:
   //par[0]=Width (scale) parameter of Landau density
   //par[1]=Most Probable (MP, location) parameter of Landau density
   //par[2]=Total area (integral -inf to inf, normalization constant)
   //par[3]=Width (sigma) of convoluted Gaussian function
   //
   //In the Landau distribution (represented by the CERNLIB approximation),
   //the maximum is located at x=-0.22278298 with the location parameter=0.
   //This shift is corrected within this function, so that the actual
   //maximum is identical to the MP parameter.
      // Numeric constants
      Double_t invsq2pi = 0.3989422804014;   // (2 pi)^(-1/2)
      Double_t mpshift  = -0.22278298;       // Landau maximum location
      // Control constants
      Double_t np = 100.0;      // number of convolution steps
      Double_t sc =   5.0;      // convolution extends to +-sc Gaussian sigmas
      // Variables
      Double_t xx;
      Double_t mpc;
      Double_t fland;
      Double_t sum = 0.0;
      Double_t xlow,xupp;
      Double_t step;
      Double_t i;
      // MP shift correction
      mpc = par[1] - mpshift * par[0];
      // Range of convolution integral
      xlow = x[0] - sc * par[3];
      xupp = x[0] + sc * par[3];
      step = (xupp-xlow) / np;
      // Convolution integral of Landau and Gaussian by sum
      for(i=1.0; i<=np/2; i++) {
         xx = xlow + (i-.5) * step;
         fland = TMath::Landau(xx,mpc,par[0]) / par[0];
         sum += fland * TMath::Gaus(x[0],xx,par[3]);
         xx = xupp - (i-.5) * step;
         fland = TMath::Landau(xx,mpc,par[0]) / par[0];
         sum += fland * TMath::Gaus(x[0],xx,par[3]);
      }
      return (par[2] * step * sum * invsq2pi / par[3]);
}

TF1 *langaufit(TH1F *his, Double_t *fitrange, Double_t *startvalues, Double_t *parlimitslo, Double_t *parlimitshi, Double_t *fitparams, Double_t *fiterrors, Double_t *ChiSqr, Int_t *NDF)
{
   // Once again, here are the Landau * Gaussian parameters:
   //   par[0]=Width (scale) parameter of Landau density
   //   par[1]=Most Probable (MP, location) parameter of Landau density
   //   par[2]=Total area (integral -inf to inf, normalization constant)
   //   par[3]=Width (sigma) of convoluted Gaussian function
   //
   // Variables for langaufit call:
   //   his             histogram to fit
   //   fitrange[2]     lo and hi boundaries of fit range
   //   startvalues[4]  reasonable start values for the fit
   //   parlimitslo[4]  lower parameter limits
   //   parlimitshi[4]  upper parameter limits
   //   fitparams[4]    returns the final fit parameters
   //   fiterrors[4]    returns the final fit errors
   //   ChiSqr          returns the chi square
   //   NDF             returns ndf
   Int_t i;
   Char_t FunName[100];
   sprintf(FunName,"Fitfcn_%s",his->GetName());
   TF1 *ffitold = (TF1*)gROOT->GetListOfFunctions()->FindObject(FunName);
   if (ffitold) delete ffitold;
   TF1 *ffit = new TF1(FunName,langaufun,fitrange[0],fitrange[1],4);
   ffit->SetParameters(startvalues);
   ffit->SetParNames("Width","MP","Area","GSigma");
   for (i=0; i<4; i++) {
      ffit->SetParLimits(i, parlimitslo[i], parlimitshi[i]);
   }
   his->Fit(FunName,"RB0");   // fit within specified range, use ParLimits, do not plot
   ffit->GetParameters(fitparams);    // obtain fit parameters
   for (i=0; i<4; i++) {
      fiterrors[i] = ffit->GetParError(i);     // obtain fit parameter errors
   }
   ChiSqr[0] = ffit->GetChisquare();  // obtain chi^2
   NDF[0] = ffit->GetNDF();           // obtain ndf
   return (ffit);              // return fit function
}


void mu2(int pdgId) {
    TFile *data = new TFile("dataOutSample.root","READ");
    TFile *mc = new TFile("mcOutSample.root","READ");
    TString hname;
    if(pdgId == 11){
        hname ="mu-";
    }
    if(pdgId == -11){
        hname ="mu+";
    }
    TKey *key = data->FindKey(hname);
    //data
    TH1 *xyECal = (TH1*)data->Get(hname + "/XYECal");
    TH1 *energy = (TH1*)data->Get(hname + "/energy");
    TH1 *mom = (TH1*)data->Get(hname + "/momentum");
    TH1 *thetaX = (TH1*)data->Get(hname + "/thetaX");
    TH1 *thetaY = (TH1*)data->Get(hname + "/thetaY");
    TH1 *hits = (TH1*)data->Get(hname + "/hits");
    TH1 *xEn = (TH1*)data->Get(hname + "/XEn");
    TH1 *dxdy = (TH1*)data->Get(hname + "/delXdelY");
    TH1 *enMom = (TH1*)data->Get(hname + "/EnergyVsmom");
    TH1F *sumEnergy = (TH1F*)data->Get(hname + "/sumEnergy1");
    TH1 *enDelThetaX = (TH1*)data->Get(hname + "/enDelThetaX");
    //TF1 *fa1 = new TF1("fa1","[0] + [1]*x + [2]*x^2 + [3]*TMath::Landau(x,[4],[5],1)",0.01,0.25);
    //fa1->SetParameters(10, 1, 1, 1, 0.17, 0.004);
    //fa1->SetLineColor(1);
    TCanvas *c1 = new TCanvas("c1",hname,1000,1000);
    c1->Divide(2,3);
    c1->cd(1);
    xyECal->SetTitle("xyECal");
    xyECal->Draw("COLZ");
    c1->cd(3);
    energy->SetTitle("Energy");
    energy->Draw();
    c1->cd(5);
    mom->SetTitle("momentum");
    //mom->GetXaxis()->SetRangeUser(0., 3.);
    mom->Draw();


    xyECal = (TH1*)mc->Get(hname + "/XYECal");
    energy = (TH1*)mc->Get(hname + "/energy");
    mom = (TH1*)mc->Get(hname + "/momentum");


    c1->cd(2);
    xyECal->SetTitle("xyECal"); //score
    xyECal->Draw("COLZ");
    c1->cd(4);
    energy->SetTitle("Energy");
    energy->Draw();
    c1->cd(6);
    mom->SetTitle("Momentum");
    mom->Draw();
    c1->Print(hname + "Comp.pdf(","pdf");

    c1->cd(1);
    thetaX->SetTitle("thetaX");
    thetaX->Draw();
    c1->cd(3);
    thetaY->SetTitle("thetaY");
    thetaY->Draw();
    c1->cd(5);
    xEn->SetTitle("X vs Energy");
    xEn->Draw("COLZ");

    thetaX = (TH1*)mc->Get(hname + "/thetaX");
    thetaY = (TH1*)mc->Get(hname + "/thetaY");
    xEn = (TH1*)mc->Get(hname + "/XEn");

    c1->cd(2);
    thetaX->SetTitle("thetaX");
    thetaX->Draw();
    c1->cd(4);
    thetaY->SetTitle("thetaY");
    thetaY->Draw();
    c1->cd(6);
    xEn->SetTitle("x vs Energy");
    xEn->Draw("COLZ");
    c1->Print(hname + "Comp.pdf","pdf");
    c1->Clear();


    c1->Divide(2,3);
    c1->cd(1);
    hits->SetTitle("hits");
    hits->Draw();
    c1->cd(3);
    dxdy->SetTitle("Delta X vs Delta Y");
    dxdy->Draw("COLZ");
    c1->cd(5);
    enMom->SetTitle("Energy Vs Momentum");
    enMom->Draw("COLZ");

    hits = (TH1*)mc->Get(hname + "/hits");
    dxdy = (TH1*)mc->Get(hname + "/delXdelY");
    enMom = (TH1*)mc->Get(hname + "/EnergyVsmom");

    c1->cd(2);
    hits->SetTitle("hits");
    hits->Draw();
    c1->cd(4);
    dxdy->SetTitle("Delta X vs Delta Y");
    dxdy->Draw("COLZ");
    c1->cd(6);
    enMom->SetTitle("Energy Vs Momentum");
    enMom->Draw("COLZ");
    c1->Print(hname + "Comp.pdf","pdf");


    c1->Clear();
    c1->Divide(2,3);
    c1->cd(1);
    sumEnergy->SetTitle("Sum of Hit Energies");
    //sumEnergy->GetYaxis()->SetRangeUser(0., 3000.);
    sumEnergy->Draw();
    TLegend *leg = new TLegend(0.1,0.7,0.48,0.9);
    leg->SetHeader("Number of Hits","C");
    leg->AddEntry(sumEnergy,"1","l");
    sumEnergy->Draw();
    for (int i = 2; i < 6; i ++){
        sumEnergy = (TH1F*)data->Get(hname + "/sumEnergy" + i);
        if(i == 2){
            //n = "Count " + (const char*)i;
            sumEnergy->SetLineColor(2);
            leg->AddEntry(sumEnergy, "2" ,"l");
        }
        else if(i == 3){
            sumEnergy->SetLineColor(3);
            leg->AddEntry(sumEnergy,"3" ,"l");
        }
        else if(i == 4){
            sumEnergy->SetLineColor(4);
            leg->AddEntry(sumEnergy,"4" ,"l");
        }
        else if(i == 5){
            sumEnergy->SetLineColor(5);
            leg->AddEntry(sumEnergy,"5" ,"l");
        }
        sumEnergy->Draw("SAMES");
    }
    leg->Draw();


    c1->cd(2);
    //sumEnergy->GetYaxis()->SetRangeUser(0., 3000.);
    //sumEnergy->Draw();
    //TLegend *leg = new TLegend(0.1,0.7,0.48,0.9);
    //leg->SetHeader("Number of Hits","C");
    //leg->AddEntry(sumEnergy,"1","l");
    //sumEnergy->Draw();
    //sumEnergy = (TH1*)mc->Get(hname + "/sumEnergy1");
    //sumEnergy->Draw();
    sumEnergy = (TH1F*)mc->Get(hname + "/sumEnergy1");
    Double_t fr[2];
    Double_t sv[4], pllo[4], plhi[4], fp[4], fpe[4];

   pllo[0]=0.001; pllo[1]=0.01; pllo[2]=1.0; pllo[3]=0.0;
   plhi[0]=10.0; plhi[1]=1.0; plhi[2]=1000000.0; plhi[3]=1.0;
   sv[0]=0.001; sv[1]=0.01; sv[2]=1.0; sv[3]=0.0;



    Double_t chisqr;
    Int_t    ndf;
    fr[0]=0.01;
    fr[1]=0.3;
    TF1 *fa1 = langaufit(sumEnergy,fr,sv,pllo,plhi,fp,fpe,&chisqr,&ndf);
    sumEnergy->SetTitle("Sum of Hit Energies");
    sumEnergy->GetYaxis()->SetRangeUser(0., 700.);
    //sumEnergy->Draw();
    gStyle->SetOptFit();
    sumEnergy->Fit(fa1);
    //fa1->Draw("lsame");
    //sumEnergy->Draw();
    for (int i = 1; i < 6; i ++){
        sumEnergy = (TH1F*)mc->Get(hname + "/sumEnergy" + i);
        if(i == 2){
            //n = "Count " + (const char*)i;
            sumEnergy->SetLineColor(2);
            //leg->AddEntry(sumEnergy, "2" ,"l");
        }
        else if(i == 3){
            sumEnergy->SetLineColor(3);
            //leg->AddEntry(sumEnergy,"3" ,"l");
        }
        else if(i == 4){
            sumEnergy->SetLineColor(4);
            //leg->AddEntry(sumEnergy,"4" ,"l");
        }
        else if(i == 5){
            sumEnergy->SetLineColor(5);
            //leg->AddEntry(sumEnergy,"5" ,"l");
        }
        //sumEnergy->Draw("SAMES");
    }
    leg->Draw();

    c1->cd(3);
    enDelThetaX->SetTitle("Energy vs Delta Theta X");
    enDelThetaX->Draw("COLZ");

    c1->cd(4);
    enDelThetaX= (TH1*)mc->Get(hname + "/enDelThetaX");
    enDelThetaX->SetTitle("Energy vs Delta Theta X");
    enDelThetaX->Draw("COLZ");


    c1->Print(hname + "Comp.pdf)","pdf");
    c1->Close();
}


int comp(){
    int pdgId = 11;
    mu2(pdgId);
    pdgId = -11;
    mu2(pdgId);
    return 0;
}