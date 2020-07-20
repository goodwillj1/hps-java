#include <TFile.h>
#include <TMath.h>
//#include <TNtuple.h>
#include <TH1.h>
#include <TH2.h>
#include <TF1.h>
//#include <TProfile.h>
#include <TCanvas.h>
#include <TLegend.h>
#include <TGraphErrors.h>
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

Double_t myFitFunction(Double_t *x,Double_t *par) {
      Double_t arg = 0;
      if (par[2]!=0) arg = (x[0] - par[1])/par[2];
      Double_t fitval = par[0]*TMath::Exp(-0.5*arg*arg);
      return fitval;
}

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

void mu2(int pdgId) {
    TFile *data = new TFile("dataOut.root","READ");
    TFile *mc = new TFile("mcOut.root","READ");
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
    TF1 *fa1 = new TF1("fa1", "[0] + [1]*x + [2]*TMath::Gaus(x,[3],[4])", 0.1, 0.26);
    double max = sumEnergy->GetMaximum();
    fa1->SetParameters(0,0,max,0.17,0.03);
    fa1->SetLineColor(1);
    fa1->SetRange(0.1,0.3);
    TCanvas *c1 = new TCanvas("c1",hname,1000,1000);
    c1->Divide(2,3);
    c1->cd(1);
    xyECal->SetTitle("xyECal");
    xyECal->Draw("COLZ");
    c1->cd(3);
    energy->SetTitle("Energy");
    energy->SetLineColor(1);
    energy->Draw();
    //energy = (TH1*)mc->Get(hname + "/energy");
    //energy->SetTitle("Energy");
    //energy->Draw("SAME");
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
    gStyle->SetOptFit();
    sumEnergy->Fit(fa1,"R");
    TLegend *leg = new TLegend(0.1,0.7,0.48,0.9);
    leg->SetHeader("Number of Hits","C");
    leg->AddEntry(sumEnergy,"1","l");
    //sumEnergy->Draw();
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
        //sumEnergy->Draw("SAMES");
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
    max = sumEnergy->GetMaximum();
    fa1->SetParameters(0,0,max,0.17,0.03);
    gStyle->SetOptFit();
    
    sumEnergy->Fit(fa1,"R+");
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
        sumEnergy->Fit(fa1,"R+");
        //sumEnergy->Draw("SAMES");
    }
    leg->Draw();

    c1->cd(3);
/*
    TF1 *f=new TF1("fa1", "[0] + [1]*x + [2]*TMath::Gaus(x,[3],[4])", 0.1, 0.26);
    TH2 *h2 = (TH2*)data->Get(hname + "/enDelThetaX");
    TGraphErrors *g = new TGraphErrors(h2);
 
    for (int bin=1; bin<=h2->GetNbinsX(); bin++) {
        TH1 *h1 = h2->ProjectionX(Form("h1_%d",bin),bin,bin);
        h1->Fit("fit","R");
        double x = h2->GetXaxis()->GetBinCenter(bin);
        double y = f->GetParameter(1);
        g->SetPoint(bin,x,y);
    }
    */
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