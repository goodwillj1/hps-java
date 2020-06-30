#include <TFile.h>
//#include <TNtuple.h>
#include <TH1.h>
#include <TH2.h>
//#include <TProfile.h>
#include <TCanvas.h>
#include <TLegend.h>
//#include <TFrame.h>
//#include <TROOT.h>
//#include <TSystem.h>
//#include <TRandom3.h>
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
    TH1 *sumHits = (TH1*)data->Get(hname + "/sumHits1");
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
    sumHits->SetTitle("Sum of Hit Energies");
    //sumHits->GetYaxis()->SetRangeUser(0., 3000.);
    sumHits->Draw();
    TLegend *leg = new TLegend(0.1,0.7,0.48,0.9);
    leg->SetHeader("Number of Hits","C");
    leg->AddEntry(sumHits,"1","l");
    sumHits->Draw();
    for (int i = 2; i < 6; i ++){
        sumHits = (TH1*)data->Get(hname + "/sumHits" + i);
        if(i == 2){
            //n = "Count " + (const char*)i;
            sumHits->SetLineColor(2);
            leg->AddEntry(sumHits, "2" ,"l");
        }
        else if(i == 3){
            sumHits->SetLineColor(3);
            leg->AddEntry(sumHits,"3" ,"l");
        }
        else if(i == 4){
            sumHits->SetLineColor(4);
            leg->AddEntry(sumHits,"4" ,"l");
        }
        else if(i == 5){
            sumHits->SetLineColor(5);
            leg->AddEntry(sumHits,"5" ,"l");
        }
        sumHits->Draw("SAMES");
    }
    leg->Draw();


    c1->cd(2);
    TLegend *leg2 = new TLegend(0.1,0.7,0.48,0.9);
    leg2->SetHeader("Number of Hits","C");
    sumHits = (TH1*)mc->Get(hname + "/sumHits1");
    sumHits->SetTitle("Sum of Hit Energies");
    sumHits->SetLineColor(1);
    leg2->AddEntry(sumHits, "1" ,"l");
    sumHits->Draw();
    sumHits = (TH1*)mc->Get(hname + "/sumHits2");
    sumHits->SetLineColor(2);
    leg2->AddEntry(sumHits, "2" ,"l");
    sumHits->Draw("SAMES");
    leg2->Draw();
    c1->Print(hname + "Comp.pdf)","pdf");
}


int comp(){
    int pdgId = 11;
    mu2(pdgId);
    pdgId = -11;
    mu2(pdgId);
    return 0;
}