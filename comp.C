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
    fa1->SetParNames("const","lin","max","MPV","sigma");
    fa1->SetLineColor(kRed);
    fa1->SetRange(0.1,0.3);
    TCanvas *c1 = new TCanvas("c1",hname,1000,1000);
    c1->Divide(2,3);
    c1->cd(1);
    xyECal->SetTitle("");
    xyECal->GetYaxis()->SetTitle("Y (mm)");
    xyECal->GetXaxis()->SetTitle("X (mm)");
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
    xyECal->SetTitle(""); //score
    xyECal->GetYaxis()->SetTitle("Y (mm)");
    xyECal->GetXaxis()->SetTitle("X (mm)");
    xyECal->GetXaxis()->SetRangeUser(-320, 100);
    xyECal->GetYaxis()->SetRangeUser(0, 100);
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
    //xEn->SetTitle("X vs Energy");
    xEn->Draw("COLZ");

    thetaX = (TH1*)mc->Get(hname + "/thetaX");
    thetaY = (TH1*)mc->Get(hname + "/thetaY");
    xEn = (TH1*)mc->Get(hname + "/XEn");

    c1->cd(2);
    //thetaX->SetTitle("thetaX");
    thetaX->Draw();
    c1->cd(4);
    //thetaY->SetTitle("thetaY");
    thetaY->Draw();
    c1->cd(6);
    //xEn->SetTitle("x vs Energy");
    xEn->Draw("COLZ");
    c1->Print(hname + "Comp.pdf","pdf");
    c1->Clear();


    c1->Divide(2,3);
    c1->cd(1);
    hits->SetTitle("");
    hits->GetYaxis()->SetTitle("Counts");
    hits->GetYaxis()->SetLabelSize(0.03);
    hits->GetXaxis()->SetTitle("Number of Hits");
    hits->Draw();
    c1->cd(3);
    dxdy->SetTitle("");
    dxdy->GetYaxis()->SetTitle("Y (mm)");
    dxdy->GetXaxis()->SetTitle("X (mm)");
    dxdy->Draw("COLZ");
    c1->cd(5);
    enMom->SetTitle("");
    enMom->GetYaxis()->SetTitle("Energy (GeV)");
    enMom->GetXaxis()->SetTitle("Momentum (GeV)");
    enMom->Draw("COLZ");

    hits = (TH1*)mc->Get(hname + "/hits");
    dxdy = (TH1*)mc->Get(hname + "/delXdelY");
    enMom = (TH1*)mc->Get(hname + "/EnergyVsmom");

    c1->cd(2);
    hits->SetTitle("");
    hits->GetYaxis()->SetTitle("Counts");
    hits->GetXaxis()->SetTitle("Number of Hits");
    hits->Draw();
    c1->cd(4);
    dxdy->SetTitle("");
    dxdy->GetYaxis()->SetTitle("Y (mm)");
    dxdy->GetXaxis()->SetTitle("X (mm)");
    dxdy->Draw("COLZ");
    c1->cd(6);
    enMom->SetTitle("");
    enMom->GetYaxis()->SetTitle("Energy (GeV)");
    enMom->GetXaxis()->SetTitle("Momentum (GeV)");
    enMom->Draw("COLZ");
    c1->Print(hname + "Comp.pdf","pdf");


    c1->Clear();
    c1->Divide(2,3);
    c1->cd(1);
    sumEnergy->SetTitle("");
    sumEnergy->GetYaxis()->SetTitle("Counts");
    sumEnergy->GetYaxis()->SetTitleSize(0.045);
    sumEnergy->GetXaxis()->SetTitle("Energy (GeV)");
    
    //sumEnergy->GetYaxis()->SetRangeUser(0., 3000.);
    gStyle->SetLabelSize(0.02);
    gStyle->SetOptFit();
    gStyle->SetOptStat(0);
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
    //leg->Draw();


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
    sumEnergy->SetTitle("");
    sumEnergy->GetYaxis()->SetTitle("Counts");
    sumEnergy->GetYaxis()->SetLabelSize(0.025);
    sumEnergy->GetXaxis()->SetTitle("Energy (GeV)");
    max = sumEnergy->GetMaximum();
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
        //sumEnergy->Fit(fa1,"R+");
        //sumEnergy->Draw("SAMES");
    }
    //leg->Draw();
    c1->Print(hname + "Comp.pdf","pdf");

    c1->Clear();
    c1->Divide(6,6);

    //TF1 *f=new TF1("f", "[0] + [1]*x + [2]*TMath::Gaus(x,[3],[4])", 0.1, 0.26);
    TH2 *h2 = (TH2*)data->Get(hname + "/enDelThetaX");
    TH1 *h1 = h2->ProjectionX(Form("h1_%d",1),0,5);
    h1->GetYaxis()->SetRangeUser(0,400);
    //h1->SetTitle("Energy Vs Delta Projection X");
    h1->GetXaxis()->SetTitle("Energy (GeV)");
    h1->GetYaxis()->SetTitle("Counts");
    c1->cd(1);
    //TGraphErrors *g = new TGraphErrors(f);
    //
    int dir = 0;
    for (int bin=1; bin<=h2->GetNbinsX(); bin++) {
            h1->SetTitle("Data");
            h1->GetXaxis()->SetTitle("Energy (GeV)");
            h1->GetYaxis()->SetTitle("Counts");
            h1 = h2->ProjectionX(Form("h1_%d",bin),bin,bin);
            TGraphErrors *g = new TGraphErrors(h1);
        if(h1->GetMaximum() > 0 ){
            c1->cd(dir);
            dir++;
            max=h1->GetMaximum();
            fa1->SetParameters(0,0,max,0.17,0.03);
            h1->Fit(fa1,"R+");
        }
        //h1->Draw();
        double x = h2->GetXaxis()->GetBinCenter(bin);
        //cout << "x=" << x << endl;
        double y = fa1->GetParameter(0);
        //cout << "y=" << y << endl;
        //cout << *f->GetParameters() << endl;
        //g->SetPoint(bin,x,y);
    }
    c1->Print(hname + "Comp.pdf","pdf");
    c1->Clear();
    c1->Divide(6,6);
    
    //TF1 *f=new TF1("f", "[0] + [1]*x + [2]*TMath::Gaus(x,[3],[4])", 0.1, 0.26);
    TH2 *mch2 = (TH2*)mc->Get(hname + "/enDelThetaX");
    TH1 *mch1 = mch2->ProjectionX(Form("mch1_%d",1),0,5);
    mch1->GetYaxis()->SetRangeUser(0,400);
    //h1->SetTitle("Energy Vs Delta Projection X");
    mch1->GetXaxis()->SetTitle("Energy (GeV)");
    mch1->GetYaxis()->SetTitle("Counts");
    c1->cd(1);
    //TGraphErrors *g = new TGraphErrors(f);
    //
    int mcdir = 0;
    for (int mcbin=1; mcbin<=h2->GetNbinsX(); mcbin++) {
            mch1->SetTitle("Sim");
            mch1->GetXaxis()->SetTitle("Energy (GeV)");
            mch1->GetYaxis()->SetTitle("Counts");
            mch1 = mch2->ProjectionX(Form("mch1_%d",mcbin),mcbin,mcbin);
            TGraphErrors *mcg = new TGraphErrors(h1);
        if(mch1->GetMaximum() > 0 ){
            c1->cd(mcdir);
            mcdir++;
            max=mch1->GetMaximum();
            fa1->SetParameters(0,0,max,0.17,0.03);
            //mch1->Fit(fa1,"R+");
            mch1->Draw();
        }
        //h1->Draw();
        double x = mch2->GetXaxis()->GetBinCenter(mcbin);
        //cout << "x=" << x << endl;
        double y = fa1->GetParameter(0);
        //cout << "y=" << y << endl;
        //cout << *f->GetParameters() << endl;
        //g->SetPoint(bin,x,y);
    }


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