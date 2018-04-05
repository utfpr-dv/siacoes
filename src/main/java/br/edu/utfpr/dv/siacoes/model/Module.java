package br.edu.utfpr.dv.siacoes.model;

public class Module {
	
	public enum SystemModule{
		GENERAL(0), SIGET(1), SIGAC(2), SIGES(3);
		
		private final int value; 
		SystemModule(int value){ 
			this.value = value; 
		}
		
		public int getValue(){ 
			return value;
		}
		
		public static SystemModule valueOf(int value){
			for(SystemModule d : SystemModule.values()){
				if(d.getValue() == value){
					return d;
				}
			}
			
			return null;
		}
		
		public String toString(){
			return this.getDescription();
		}
		
		public String getDescription(){
			switch(this){
				case SIGAC:
					return "SIGAC - Sistema de Gestão de Atividades Complementares";
				case SIGES:
					return "SIGES - Sistema de Gestão de Estágios";
				case SIGET:
					return "SIGET - Sistema de Gestão de TCC's";
				default:
					return "SIACOES - Sistema Integrado de Atividades Complementares, Orientações e Estágios";
			}
		}
		
		public String getShortDescription(){
			switch(this){
				case SIGAC:
					return "Atividades Complementares";
				case SIGES:
					return "Estágios";
				case SIGET:
					return "TCC";
				default:
					return "Geral";
			}
		}
		
	}

}
